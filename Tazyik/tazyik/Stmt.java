package tazyik;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitRiverDeclStmt(RiverDecl stmt);
    R visitFlowDeclStmt(FlowDecl stmt);
    R visitCapacityDeclStmt(CapacityDecl stmt);
    R visitFlowOutDeclStmt(FlowOutDecl stmt);
    R visitRiverUpdateStmt(RiverUpdate stmt);
    R visitDamDeclStmt(DamDecl stmt);
  }
  static class RiverDecl extends Stmt {
    RiverDecl(Token name, Expr expr) {
      this.name = name;
      this.expr = expr;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitRiverDeclStmt(this);
    }

    final Token name;
    final Expr expr;
  }
  static class FlowDecl extends Stmt {
    FlowDecl(Token name, Expr expr) {
      this.name = name;
      this.expr = expr;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFlowDeclStmt(this);
    }

    final Token name;
    final Expr expr;
  }
  static class CapacityDecl extends Stmt {
    CapacityDecl(Token name, double value) {
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCapacityDeclStmt(this);
    }

    final Token name;
    final double value;
  }
  static class FlowOutDecl extends Stmt {
    FlowOutDecl(Token name, double value) {
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFlowOutDeclStmt(this);
    }

    final Token name;
    final double value;
  }
  static class RiverUpdate extends Stmt {
    RiverUpdate(Token name, Expr expr) {
      this.name = name;
      this.expr = expr;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitRiverUpdateStmt(this);
    }

    final Token name;
    final Expr expr;
  }
  static class DamDecl extends Stmt {
    DamDecl(Token name, double capacity, double releasePercent) {
      this.name = name;
      this.capacity = capacity;
      this.releasePercent = releasePercent;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitDamDeclStmt(this);
    }

    final Token name;
    final double capacity;
    final double releasePercent;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
