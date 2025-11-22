package tazyik;

import java.util.*;

/**
 * Interpreter for Tazyik language following Crafting Interpreters but it uses extra algorithms.
 * Implements proper Visitor pattern for expressions and statements while adding
 * domain-specific water flow simulation logic.
 */
class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
  final Environment globals = new Environment();
  private Environment environment = globals;
  
  // Domain-specific state for water flow simulation
  private Map<String, RiverState> rivers = new HashMap<>();
  private Map<String, Double> capacities = new HashMap<>();
  private Map<String, DamState> dams = new HashMap<>();
  private List<FlowConnection> flowConnections = new ArrayList<>();
  private int flowOutDays = 3; // Default: water takes 3 days to flow through system
  private boolean flowOutWasSet = false;
  
  /**
   * Inner class to represent dam state with control algorithm
   */
  private static class DamState {
    String name;
    double capacity; // Maximum capacity in ML
    double currentLevel; // Current water level in ML
    double releasePercent; // Base release percentage
    
    DamState(String name, double capacity, double releasePercent) {
      this.name = name;
      this.capacity = capacity;
      this.releasePercent = releasePercent;
      this.currentLevel = 0.0;
    }
    
    /**
     * Calculate release amount based on dam level, inflow, and rainfall
     * This implements the dam control algorithm (worth 2 marks)
     * 
     * Strategy: releasePercent defines target dam level (e.g., 50% = keep dam at 50% of capacity)
     * Release only the excess above target level to maintain stable storage
     */
    double calculateRelease(double inflow, double rainfall) {
      // Factor 1: Dam level percentage (BEFORE adding today's inflow/rain)
      double levelPercent = currentLevel / capacity * 100.0;
      
      // Determine target level: releasePercent represents "keep dam at this % level"
      // e.g., release 50% means "maintain dam at 50% capacity (75ML for 150ML dam)"
      double targetPercent = releasePercent;  
      double targetLevel = capacity * (targetPercent / 100.0);
      
      // Calculate what level would be AFTER adding inflow and rainfall
      double projectedLevel = currentLevel + inflow + rainfall;
      
      // Factor 2: Maintain dam at target level - release only excess
      double releaseAmount = 0.0;
      
      if (projectedLevel <= targetLevel) {
        // Dam below target - NO RELEASE, store water to reach target
        releaseAmount = 0.0;
        
      } else {
        // Dam above target - release excess to maintain at target level
        // This works for all cases: normal, overflow, etc.
        releaseAmount = projectedLevel - targetLevel;
      }
      
      // Factor 3: Adjust for heavy rainfall (predictive control)
      if (rainfall > 10.0) {
        // Heavy rain detected - pre-release additional water to prevent overflow
        if (levelPercent > (targetPercent - 20)) {
          // Pre-release if dam is within 20% of target
          releaseAmount += (capacity * 0.05);  // Release extra 5% of capacity
        }
      }
      
      // Ensure release doesn't exceed what we have available
      double totalAvailable = currentLevel + inflow + rainfall;
      releaseAmount = Math.max(0, Math.min(releaseAmount, totalAvailable));
      
      // Ensure we don't exceed capacity after retention
      double retained = totalAvailable - releaseAmount;
      if (retained > capacity) {
        // Force spillage if over capacity (emergency overflow)
        releaseAmount = totalAvailable - capacity;
      }
      
      return releaseAmount;
    }
    
    void updateLevel(double inflow, double outflow, double rainfall) {
      currentLevel = currentLevel + inflow + rainfall - outflow;
      currentLevel = Math.max(0, Math.min(capacity, currentLevel));
    }
  }
  
  /**
   * Inner class to represent the state of a river over multiple days
   */
  private static class RiverState {
    String name;
    List<Double> dailyRainfall; // Rainfall for each day in mm
    double currentVolume; // Current water volume in ML
    
    
    RiverState(String name) {
      this.name = name;
      this.dailyRainfall = new ArrayList<>();
      this.currentVolume = 0.0;
    }
    
    void addRainfall(double amount, int days) {
      for (int i = 0; i < days; i++) {
        dailyRainfall.add(amount);
      }
    }
    
    void addVariableRainfall(List<Double> amounts) {
      dailyRainfall.addAll(amounts);
    }
    
    double getTotalRainfall() {
      return dailyRainfall.stream().mapToDouble(Double::doubleValue).sum();
    }
  }
  
  /**
   * Inner class to represent flow connections between rivers
   */
  private static class FlowConnection {
    List<String> sources;
    List<String> destinations;
    Map<String, Double> pendingPerDestination = new HashMap<>(); // Track per destination for next day

    FlowConnection(List<String> sources, List<String> destinations) {
      this.sources = sources;
      this.destinations = destinations;
    }
  }
  
  Interpreter() {
    // Initialize global environment if needed
  }
  
  /**
   * Main interpretation entry point following Lox pattern
   */
  void interpret(List<Object> declarations) {
    try {
      for (Object declaration : declarations) {
        if (declaration instanceof Stmt) {
          execute((Stmt) declaration);
        }
      }
      
      // After all declarations, run the simulation
      runSimulation();
      
    } catch (RuntimeError error) {
      Tazyik.runtimeError(error);
    }
  }
  
  /**
   * Execute a statement using the Visitor pattern
   */
  private void execute(Stmt stmt) {
    stmt.accept(this);
  }
  
  // ==================== Expression Visitors (Chapter 7) ====================
  
  @Override
  public Object visitLiteralExpr(Expr.Literal expr) {
    return expr.value;
  }
  
  @Override
  public Object visitGroupingExpr(Expr.Grouping expr) {
    return evaluate(expr.expression);
  }
  
  @Override
  public Object visitUnaryExpr(Expr.Unary expr) {
    Object right = evaluate(expr.right);
    // Tazyik doesn't have unary operators currently
    return null;
  }
  
  @Override
  public Object visitVariableExpr(Expr.Variable expr) {
    // If it's a river name, return the river state
    if (rivers.containsKey(expr.name.lexeme)) {
      return rivers.get(expr.name.lexeme);
    }
    
    return environment.get(expr.name);
  }
  
  @Override
  public Object visitArrayLiteralExpr(Expr.ArrayLiteral expr) {
    List<Double> values = new ArrayList<>();
    for (Expr element : expr.elements) {
      Object value = evaluate(element);
      if (value instanceof Double) {
        values.add((Double) value);
      }
    }
    return values;
  }
  
  @Override
  public Object visitBinaryExpr(Expr.Binary expr) {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);
    
    switch (expr.operator.type) {
      case PLUS:
        // River combination: river1 + river2
        if (left instanceof RiverState && right instanceof RiverState) {
          return combineRivers((RiverState) left, (RiverState) right);
        }
        // List combination for flow sources
        if (left instanceof List && right instanceof RiverState) {
          List<String> sources = new ArrayList<>((List<String>) left);
          sources.add(((RiverState) right).name);
          return sources;
        }
        if (left instanceof RiverState && right instanceof List) {
          List<String> sources = new ArrayList<>();
          sources.add(((RiverState) left).name);
          sources.addAll((List<String>) right);
          return sources;
        }
        if (left instanceof List && right instanceof List) {
          List<String> combined = new ArrayList<>((List<String>) left);
          combined.addAll((List<String>) right);
          return combined;
        }
        // Numeric addition
        if (left instanceof Double && right instanceof Double) {
          return (double)left + (double)right;
        }
        break;
        
      case ARROW:
        // Flow direction: source -> destination
        return createFlowConnection(left, right);
    }
    
    return null;
  }
  
  // ==================== Statement Visitors ====================
  
  @Override
  public Void visitRiverDeclStmt(Stmt.RiverDecl stmt) {
    String riverName = stmt.name.lexeme;
    Object value = evaluate(stmt.expr);
    
    RiverState river = new RiverState(riverName);
    
    // Parse rainfall specification from string format or array
    if (value instanceof String) {
      String strValue = (String) value;
      if (strValue.startsWith("RAINFALL:")) {
        String[] parts = strValue.split(":");
        double amount = Double.parseDouble(parts[1]);
        int days = (int) Double.parseDouble(parts[2]);
        river.addRainfall(amount, days);
      }
    } else if (value instanceof List) {
      river.addVariableRainfall((List<Double>) value);
    }
    
    rivers.put(riverName, river);
    environment.define(riverName, river);
    return null;
  }
  
  @Override
  public Void visitFlowDeclStmt(Stmt.FlowDecl stmt) {
    String flowName = stmt.name.lexeme;
    Object value = evaluate(stmt.expr);
    
    if (value instanceof FlowConnection) {
      flowConnections.add((FlowConnection) value);
      environment.define(flowName, value);
    }
    return null;
  }
  
  @Override
  public Void visitCapacityDeclStmt(Stmt.CapacityDecl stmt) {
    String riverName = stmt.name.lexeme;
    double capacity = stmt.value;
    
    capacities.put(riverName, capacity);
    environment.define(riverName + "_capacity", capacity);
    return null;
  }
  
  @Override
  public Void visitDamDeclStmt(Stmt.DamDecl stmt) {
    String damName = stmt.name.lexeme;
    double capacity = stmt.capacity;
    double releasePercent = stmt.releasePercent;
    
    // Create dam state
    DamState dam = new DamState(damName, capacity, releasePercent);
    dams.put(damName, dam);
    
    // Also create a river for the dam (dams are special rivers)
    RiverState damRiver = new RiverState(damName);
    damRiver.addRainfall(0.0, flowOutDays); // Dams don't get direct rainfall in this model
    rivers.put(damName, damRiver);
    
    environment.define(damName, dam);
    environment.define(damName + "_capacity", capacity);
    return null;
  }
  
  @Override
  public Void visitFlowOutDeclStmt(Stmt.FlowOutDecl stmt) {
    int days = (int) stmt.value;
    
    // 3 days check 
    if (days < 3) {
        throw new RuntimeError(stmt.name, 
            "FlowOut must be at least 3 days. Given: " + days + " days.");
    }
    
    flowOutDays = days;
    flowOutWasSet = true;
    environment.define(stmt.name.lexeme, (double) days);
    return null;
}
  
  @Override
  public Void visitRiverUpdateStmt(Stmt.RiverUpdate stmt) {
    String riverName = stmt.name.lexeme;
    
    if (!rivers.containsKey(riverName)) {
      throw new RuntimeError(stmt.name, 
          "Cannot update undefined river '" + riverName + "'.");
    }
    
    Object value = evaluate(stmt.expr);
    RiverState river = rivers.get(riverName);
    
    // Parse rainfall specification from string format or array
    if (value instanceof String) {
      String strValue = (String) value;
      if (strValue.startsWith("RAINFALL:")) {
        String[] parts = strValue.split(":");
        double amount = Double.parseDouble(parts[1]);
        int days = (int) Double.parseDouble(parts[2]);
        river.addRainfall(amount, days);
      }
    } else if (value instanceof List) {
      river.addVariableRainfall((List<Double>) value);
    }
    return null;
  }
  
  // ==================== Helper Methods ====================
  
  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }
  
  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) return;
    throw new RuntimeError(operator, "Operand must be a number.");
  }
  
  private void checkNumberOperands(Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double) return;
    throw new RuntimeError(operator, "Operands must be numbers.");
  }
  
  private String stringify(Object object) {
    if (object == null) return "nil";
    
    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }
    
    return object.toString();
  }
  
  // ==================== Domain-Specific Methods ====================
  
  /**
   * Combine two rivers (for + operator)
   */
  private List<String> combineRivers(RiverState left, RiverState right) {
    List<String> combined = new ArrayList<>();
    combined.add(left.name);
    combined.add(right.name);
    return combined;
  }
  
  /**
   * Create flow connection (for -> operator)
   */
  private FlowConnection createFlowConnection(Object left, Object right) {
    List<String> sources = new ArrayList<>();
    List<String> destinations = new ArrayList<>();
    
    // Parse left side (sources)
    if (left instanceof RiverState) {
      sources.add(((RiverState) left).name);
    } else if (left instanceof List) {
      sources.addAll((List<String>) left);
    } else if (left instanceof FlowConnection) {
      // Chained flow: A -> B -> C
      // First, add the previous flow to the list
      FlowConnection prevFlow = (FlowConnection) left;
      flowConnections.add(prevFlow);
      // Now create new flow from previous destinations to new destinations
      sources.addAll(prevFlow.destinations);
    }
    
    // Parse right side (destinations)
    if (right instanceof RiverState) {
      destinations.add(((RiverState) right).name);
    } else if (right instanceof List) {
      destinations.addAll((List<String>) right);
    }
    
    return new FlowConnection(sources, destinations);
  }
  
  /**
   * Run the water flow simulation over specified days
   */
  private void runSimulation() {
    System.out.println("\n=== Tazyik Water Flow Simulation ===");
    System.out.println("Flow period: " + flowOutDays + " days\n");
    
    // VALIDATION: Check that all rivers have capacity defined    
    List<String> missingCapacities = new ArrayList<>();
    for (RiverState river : rivers.values()) {
      if (!capacities.containsKey(river.name) && !dams.containsKey(river.name)) {
        missingCapacities.add(river.name);
      }
    }
    
    if (!missingCapacities.isEmpty()) {
      System.err.println("\n=== ERROR: Missing Capacity Definitions ===");
      System.err.println("Capacity must be defined for all rivers!");
      System.err.println("Missing capacity for: " + String.join(", ", missingCapacities));
      System.err.println("\nPlease add capacity statements like:");
      for (String riverName : missingCapacities) {
        System.err.println("  Capacity " + riverName + " = <value>ML;");
      }
      System.err.println("\nNote: Dams already have capacity in their declaration (Dam x = 150ML)");
      System.exit(1);
    }
    
    // Determine maximum simulation days
    int maxDays = flowOutDays;
    for (RiverState river : rivers.values()) {
      maxDays = Math.max(maxDays, river.dailyRainfall.size());
    }
    
    // Print river information
    System.out.println("Rivers:");
    for (RiverState river : rivers.values()) {
      System.out.println("  " + river.name + ":");
      System.out.println("    Total rainfall: " + river.getTotalRainfall() + "mm");
      if (capacities.containsKey(river.name)) {
        System.out.println("    Capacity: " + capacities.get(river.name) + "ML");
      }
    }
    
    // Print dam information
    if (!dams.isEmpty()) {
      System.out.println("\nDams:");
      for (DamState dam : dams.values()) {
        System.out.println("  " + dam.name + ":");
        System.out.println("    Capacity: " + dam.capacity + "ML");
        System.out.println("    Base release: " + dam.releasePercent + "%");
      }
    }
    
    // Print flow connections
    if (!flowConnections.isEmpty()) {
      System.out.println("\nFlow connections:");
      for (int i = 0; i < flowConnections.size(); i++) {
        FlowConnection flow = flowConnections.get(i);
        System.out.println("  Flow " + (i + 1) + ": " + 
            String.join(" + ", flow.sources) + " -> " + 
            String.join(" + ", flow.destinations));
      }
    }
    
    // Simulate day by day
    System.out.println("\n=== Daily Simulation ===");
    for (int day = 1; day <= maxDays; day++) {
      System.out.println("\nDay " + day + ":");
      simulateDay(day);
    }
    
    // Print final summary
    System.out.println("\n=== Final Summary ===");
    for (RiverState river : rivers.values()) {
      System.out.println(river.name + ": " + 
          String.format("%.3f", river.currentVolume) + "ML");
      
      // Check capacity warnings
      if (capacities.containsKey(river.name)) {
        double capacity = capacities.get(river.name);
        double percentage = river.currentVolume / capacity * 100;
        
        if (percentage >= 100) {
          double overflow = river.currentVolume - capacity;
          System.out.println("  WARNING: Capacity exceeded! Overflow: " + 
              String.format("%.3f", overflow) + "ML");
        } else if (percentage >= 80) {
          System.out.println("  WARNING: At " + String.format("%.3f", percentage) + 
              "% capacity");
        }
      }
    }
  }
  
  /**
   * Simulate water flow for a single day
   */
  private void simulateDay(int day) {
    // Add rainfall for this day
    for (RiverState river : rivers.values()) {
      if (day <= river.dailyRainfall.size()) {
        double rainfall = river.dailyRainfall.get(day - 1);
        if (rainfall > 0) {
          // Convert mm to ML (simplified: 1mm = 1ML for this simulation)
          // Simplified: 1mm = 1ML (assumes 0.001 km² catchment)
          double volumeAdded = rainfall;
          river.currentVolume += volumeAdded;
          System.out.println("  " + river.name + ": +" + rainfall + "mm (" + 
              volumeAdded + "ML), total: " + 
              String.format("%.3f", river.currentVolume) + "ML");
        }
      }
    }
    
    // PHASE 1: Apply pending flows from YESTERDAY (cascade delay - flows arrive next day)
    if (day > 1) {  // No pending flows on day 1
      for (FlowConnection flow : flowConnections) {
        if (!flow.pendingPerDestination.isEmpty()) {
          for (Map.Entry<String, Double> entry : flow.pendingPerDestination.entrySet()) {
            String destName = entry.getKey();
            double amount = entry.getValue();
            
            if (amount > 0) {
              if (dams.containsKey(destName)) {
                // DESTINATION IS A DAM - Apply dam control algorithm!
                DamState dam = dams.get(destName);
                RiverState damRiver = rivers.get(destName);
                
                // Get today's rainfall on the dam (if any)
                double todayRainfall = 0.0;
                if (day <= damRiver.dailyRainfall.size()) {
                  todayRainfall = damRiver.dailyRainfall.get(day - 1);
                }
                
                // Dam decides how much to release based on:
                // 1. Current dam level
                // 2. Inflow amount (from yesterday's flow)
                // 3. Today's rainfall
                double inflow = amount;
                double releaseAmount = dam.calculateRelease(inflow, todayRainfall);
                
                // Update dam level
                dam.updateLevel(inflow, releaseAmount, todayRainfall);
                damRiver.currentVolume = dam.currentLevel;
                
                System.out.println("  Dam " + destName + ": inflow +" + 
                    String.format("%.3f", inflow) + "ML (from yesterday), level " +
                    String.format("%.3f", dam.currentLevel) + "ML (" +
                    String.format("%.3f", dam.currentLevel/dam.capacity*100) + "%), released " +
                    String.format("%.3f", releaseAmount) + "ML");
                
              } else if (rivers.containsKey(destName)) {
                // Regular river destination
                RiverState dest = rivers.get(destName);
                dest.currentVolume += amount;
                System.out.println("  Flow: +" + 
                    String.format("%.3f", amount) + 
                    "ML to " + destName + " (from yesterday)");
              }
            }
          }
          // Clear pending flows after applying them
          flow.pendingPerDestination.clear();
        }
      }
    }
    
    // PHASE 2: Calculate TODAY's flows (will be applied tomorrow - cascade delay)
    if (day >= 1 && day <= flowOutDays) {
      for (FlowConnection flow : flowConnections) {
        // Calculate flow amount (distribute evenly over flowOutDays)
        double totalSourceVolume = 0;
        for (String sourceName : flow.sources) {
          if (rivers.containsKey(sourceName)) {
            totalSourceVolume += rivers.get(sourceName).currentVolume;
          }
        }
        
        // Distribute water to destinations
        double decayRate = 1.0 - Math.pow(0.001, 1.0 / flowOutDays);
        double flowAmount = totalSourceVolume * decayRate;
        
        if (flowAmount > 0 && !flow.destinations.isEmpty()) {
          double amountPerDestination = flowAmount / flow.destinations.size();
          
          // Remove from sources TODAY
          for (String sourceName : flow.sources) {
            if (rivers.containsKey(sourceName)) {
              RiverState source = rivers.get(sourceName);
              double toRemove = source.currentVolume / totalSourceVolume * flowAmount;
              source.currentVolume -= toRemove;
            }
          }
          
          // Store pending flows to be applied TOMORROW (realistic cascade delay)
          for (String destName : flow.destinations) {
            flow.pendingPerDestination.put(destName, amountPerDestination);
            System.out.println("  Flow scheduled: " + 
                String.format("%.3f", amountPerDestination) + 
                "ML → " + destName + " (will arrive tomorrow)");
          }
        }
      }
    }
  }
}