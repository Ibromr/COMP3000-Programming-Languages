package tazyik;

import java.util.ArrayList;
import java.util.List;

class Parser {

  private static final class ParseError extends RuntimeException {
  }

  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  List<Object> parseProgram() {
    List<Object> statements = new ArrayList<>();
    while (!isAtEnd()) {
      statements.add(declaration());
    }
    return statements;
  }

  private Object declaration() {
    if (match(TokenType.FLOWOUT))
      return flowOutDecl();
    if (match(TokenType.DAM))
      return damDecl();
    if (match(TokenType.RIVER))
      return riverDecl();
    if (match(TokenType.FLOW))
      return flowDecl();
    if (match(TokenType.CAPACITY))
      return capacityDecl();
    if (check(TokenType.IDENTIFIER))
      return riverUpdate();
    throw error(peek(), "Expect declaration.");
  }

  private Stmt.FlowOutDecl flowOutDecl() {
    Token name = consume(TokenType.IDENTIFIER, "Expect FlowOut name.");
    consume(TokenType.EQUAL, "Expect '=' after FlowOut name.");
    Token value = consume(TokenType.NUMBER, "Expect number after '='.");
    consume(TokenType.SEMICOLON, "Expect ';' after FlowOut declaration.");
    return new Stmt.FlowOutDecl(name, (Double) value.literal);
  }

  private Stmt.RiverDecl riverDecl() {
    Token name = consume(TokenType.IDENTIFIER, "Expect river name.");
    consume(TokenType.EQUAL, "Expect '=' after river name.");
    Expr expr = expression();
    consume(TokenType.SEMICOLON, "Expect ';' after river declaration.");
    return new Stmt.RiverDecl(name, expr);
  }

  private Stmt.RiverUpdate riverUpdate() {
    Token name = consume(TokenType.IDENTIFIER, "Expect river name.");
    consume(TokenType.EQUAL, "Expect '=' after river name.");
    Expr expr = expression();
    consume(TokenType.SEMICOLON, "Expect ';' after river update.");
    return new Stmt.RiverUpdate(name, expr);
  }

  private Stmt.FlowDecl flowDecl() {
    Token name = consume(TokenType.IDENTIFIER, "Expect flow name.");
    consume(TokenType.EQUAL, "Expect '=' after flow name.");
    Expr expr = expression();
    consume(TokenType.SEMICOLON, "Expect ';' after flow declaration.");
    return new Stmt.FlowDecl(name, expr);
  }

  private Stmt.CapacityDecl capacityDecl() {
    Token name = consume(TokenType.IDENTIFIER, "Expect capacity name.");
    consume(TokenType.EQUAL, "Expect '=' after capacity name.");
    Token value = consume(TokenType.NUMBER, "Expect number after '='.");
    consume(TokenType.ML, "Expect 'ML' after number.");
    consume(TokenType.SEMICOLON, "Expect ';' after capacity declaration.");
    return new Stmt.CapacityDecl(name, (Double) value.literal);
  }

  private Stmt.DamDecl damDecl() {
    Token name = consume(TokenType.IDENTIFIER, "Expect dam name.");
    consume(TokenType.EQUAL, "Expect '=' after dam name.");
    Token capacity = consume(TokenType.NUMBER, "Expect capacity number.");
    consume(TokenType.ML, "Expect 'ML' after capacity.");
    
    // Make release parameter optional - default to 80%
    double releasePercent = 80.0;
    if (match(TokenType.RELEASE)) {
      Token releaseToken = consume(TokenType.NUMBER, "Expect release percentage.");
      consume(TokenType.PERCENT, "Expect '%' after release percentage.");
      releasePercent = (Double) releaseToken.literal;
    }
    
    consume(TokenType.SEMICOLON, "Expect ';' after dam declaration.");
    return new Stmt.DamDecl(name, (Double) capacity.literal, releasePercent);
  }

  private Expr expression() {
    return flowExpr();
  }

  private Expr flowExpr() {
    Expr expr = addExpr();
    while (match(TokenType.ARROW)) {
      Token operator = previous();
      Expr right = addExpr();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr addExpr() {
    Expr expr = primary();
    while (match(TokenType.PLUS)) {
      Token operator = previous();
      Expr right = primary();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr primary() {
    if (match(TokenType.NUMBER)) {
      Token numberToken = previous();
      // Multi-day rainfall: NUMBER ( NUMBER ) mm
      if (match(TokenType.LPAREN)) {
        Token daysToken = consume(TokenType.NUMBER, "Expect number of days.");
        consume(TokenType.RPAREN, "Expect ')' after days.");
        consume(TokenType.MM, "Expect 'mm' after rainfall.");
        // Store rainfall info as a specially formatted string
        String rainfallInfo = "RAINFALL:" + numberToken.literal + ":" + daysToken.literal;
        return new Expr.Literal(rainfallInfo);
      }
      // Simple rainfall: NUMBER mm
      if (match(TokenType.MM)) {
        String rainfallInfo = "RAINFALL:" + numberToken.literal + ":1";
        return new Expr.Literal(rainfallInfo);
      }
      return new Expr.Literal(numberToken.literal);
    }
    if (match(TokenType.LBRACKET)) {
      // Array rainfall: [NUMBER (, NUMBER)* ] mm
      List<Expr> elements = new ArrayList<>();
      do {
        if (check(TokenType.NUMBER)) {
          elements.add(new Expr.Literal(advance().literal));
        }
      } while (match(TokenType.COMMA));
      consume(TokenType.RBRACKET, "Expect ']' after rainfall array.");
      consume(TokenType.MM, "Expect 'mm' after rainfall array.");
      return new Expr.ArrayLiteral(elements);
    }
    if (match(TokenType.IDENTIFIER)) {
      return new Expr.Variable(previous());
    }
    if (match(TokenType.LPAREN)) {
      Expr expr = expression();
      consume(TokenType.RPAREN, "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }
    throw error(peek(), "Expect expression.");
  }

  // Utility methods
  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }
    return false;
  }

  private Token consume(TokenType type, String message) {
    if (check(type))
      return advance();
    throw error(peek(), message);
  }

  private boolean check(TokenType type) {
    if (isAtEnd())
      return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd())
      current++;
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type == TokenType.EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

  // Allows previous(-n) for lookback
  private Token previous(int offset) {
    return tokens.get(current - 1 - offset);
  }

  private ParseError error(Token token, String message) {
    Tazyik.error(token.line, " at '" + token.lexeme + "': " + message);
    return new ParseError();
  }
}
