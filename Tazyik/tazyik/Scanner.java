package tazyik;

import static tazyik.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

  private static final Map<String, TokenType> keywords;
  static {
    keywords = new HashMap<>();
    keywords.put("River", RIVER);
    keywords.put("Flow", FLOW);
    keywords.put("Capacity", CAPACITY);
    keywords.put("FlowOut", FLOWOUT);
    keywords.put("Dam", DAM);
    keywords.put("release", RELEASE);
    // Add more keywords if needed
  }

  public Scanner(String source) {
    this.source = source;
  }

  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }
    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(':
        addToken(LPAREN);
        break;
      case ')':
        addToken(RPAREN);
        break;
      case '[':
        addToken(LBRACKET);
        break;
      case ']':
        addToken(RBRACKET);
        break;
      case ',':
        addToken(COMMA);
        break;
      case '+':
        addToken(PLUS);
        break;
      case ';':
        addToken(SEMICOLON);
        break;
      case '=':
        addToken(EQUAL);
        break;
      case '-':
        if (match('>')) {
          addToken(ARROW);
        }
        break;
      case '%':
        addToken(PERCENT);
        break;
      case ' ':
      case '\r':
      case '\t':
        // Ignore whitespace
        break;
      case '\n':
        line++;
        break;
      default:
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifierOrUnit();
        } else {
          Tazyik.error(line, "Unexpected character: " + c);
        }
        break;
    }
  }

  private void identifierOrUnit() {
    while (isAlphaNumeric(peek()))
      advance();
    String text = source.substring(start, current);
    
    // Check for % symbol immediately after number for percentage
    if (peek() == '%') {
      advance();
      addToken(PERCENT);
      return;
    }
    
    TokenType type = keywords.get(text);
    if (type != null) {
      addToken(type);
    } else if (text.equals("mm")) {
      addToken(MM);
    } else if (text.equals("ML")) {
      addToken(ML);
    } else {
      addToken(IDENTIFIER);
    }
  }

  private void number() {
    while (isDigit(peek()))
      advance();
    if (peek() == '.' && isDigit(peekNext())) {
      advance();
      while (isDigit(peek()))
        advance();
    }
    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private char advance() {
    return source.charAt(current++);
  }

  private boolean match(char expected) {
    if (isAtEnd())
      return false;
    if (source.charAt(current) != expected)
      return false;
    current++;
    return true;
  }

  private char peek() {
    if (isAtEnd())
      return '\0';
    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length())
      return '\0';
    return source.charAt(current + 1);
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
        (c >= 'A' && c <= 'Z') ||
        c == '_';
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }
}
