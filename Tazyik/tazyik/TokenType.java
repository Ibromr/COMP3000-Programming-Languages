package tazyik;

enum TokenType {
  // Keywords
  RIVER, FLOW, CAPACITY, FLOWOUT, DAM, RELEASE,

  // Literals
  IDENTIFIER, NUMBER, MM, ML, PERCENT,

  // Operators and punctuation
  PLUS, ARROW, EQUAL, COMMA, SEMICOLON,
  LPAREN, RPAREN, LBRACKET, RBRACKET,

  // End of file
  EOF
}