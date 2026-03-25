grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;


//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
DIV: '/';
ASSIGNMENT_OPERATOR: ':=';

//--- PARSER: ---
stylesheet: assignment* style_rule* EOF | EOF;

style_rule: identity OPEN_BRACE body CLOSE_BRACE;

identity: ID_IDENT | CLASS_IDENT | (LOWER_IDENT | CAPITAL_IDENT);

body: body_content;

body_content: (declaration | if_else)*;

declaration: prop_name COLON prop_value SEMICOLON;
assignment: var_name ASSIGNMENT_OPERATOR var_value SEMICOLON;
expression: (SCALAR | PIXELSIZE | var_name) | expression (MUL | DIV) expression | expression (PLUS | MIN) expression;

if_else: if_clause else_clause | if_clause ;
if_clause: IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE OPEN_BRACE body CLOSE_BRACE;
else_clause: ELSE OPEN_BRACE body CLOSE_BRACE;

var_name: CAPITAL_IDENT;
var_value: COLOR | PIXELSIZE | TRUE | FALSE;

prop_name: LOWER_IDENT;
prop_value: PIXELSIZE | COLOR | expression;


