grammar dart;

@header{
    package antlr;
}

//PARSER
prog : (class | meth)*                                     #Program;

    //class rule
class : CLASS  ID '{' (decl | meth)* '}'                   #Classes;

    //method rule
meth : (TYPE | ID)  ID '(' ((TYPE | ID)  ID (',' (TYPE | ID)  ID)*)* ')' '{' stat* RETURN (expression | widget)? ';' '}'        #Method;

    //statment rule
stat
     : assign                                              # AssignStat
     | decl                                                # DeclareStat
     | 'print' '(' ((expression | functionCall | objectFunctionCall) (',' (expression | functionCall | objectFunctionCall))*)*  ')' ';'   # printOutput
     | if                                                  # IfStat
     | for                                                 # ForStat
     | while                                               # WhileStat
     | functionCall                                        #CallingFunctionStat
     | objectFunctionCall                                  #CallingFuncitonFromObjectStat
     | declareObject                                       #DeclaringObjectStat
     | instanObject                                        #InstantiatingObjectStat
     ;

    //declaration rule
decl : TYPE ID ('=' expression)? ';'                      #Declaration;

    //assignment rule
assign : ID '=' expression ';'                             #Assignment;

    //if rule
if : IF '(' cond ((OR | AND) cond)* ')' ('{' stat* '}' | stat) (ELSE stat | ELSE '{' stat* '}')?    #IfCondition;

    //for rule
for : FOR '(' ID '=' expression ';'  ID ('<' | '<=' | '>' | '>=') expression ';' ID '=' expression ')' '{' stat* '}' #ForLoop;

    //while rule
while : WHILE '(' cond ((OR | AND) cond)* ')' '{' stat* '}'     #WhileLoop;

    //calling function rule
functionCall : ID '(' ((expression | widget)(',' (expression | widget))*)* ')'           #CallingFunction;

    //calling function from object rule
objectFunctionCall : ID '.' functionCall                        #CallingFuncitonFromObject;

    //declare object
declareObject : ID ID ('=' instanObject | ';')                      #DeclaringObject;

    //instantiat object
instanObject : NEW ID '(' ((expression | functionCall | objectFunctionCall) (',' (expression | functionCall | objectFunctionCall))*)* ')' ';'  #InstantiatingObject;

    //condition rule
cond : expression ('==' | '>=' | '<=' | '!=' | '>' | '<') expression     #Condition;

    //expression rules
expression
    : '(' expression ')'                # parenExpression
    | expression '*' expression         # multiplication
    | expression '/' expression         # divide
    | expression '+' expression         # addition
    | expression '-' expression         # subtract
    | STRING                            # string
    | ID                                # identifier
    | INT                               # integer
    | BOOL                              # bool
    | FLOAT                             # float
    ;

//parser rules for flutter

// widget rule
widget
  : //stateless
   material
  | scaffold
  | appBar
  | body
  | row
  | column
  | text
  | image
  | listView
  ;

// stateless widget rule
//stateless
//  : 'class' ID 'extends' 'StatelessWidget' '{' 'const' ID '({' 'Key' '?' 'key' '})' ':' 'super' '(' 'key' ':' 'key' ')' ';' #statelessWidget
//  ;

// material app rule
material
  : 'MaterialApp' '(' 'home' ':' scaffold ')' #materialWidget
  ;

// scaffold rule
scaffold
  : 'Scaffold' '(' 'appBar' ':' appBar ',' 'body' ':' body ')' #scaffoldWidget
  ;

// app bar rule
appBar
  : 'AppBar' '(' 'title' ':' text ')' #appBarWidget
  ;

// body rule
body
  : row
  | column
  ;

// row rule
row
  : 'Row' '(' 'children' ':' '[' widget (',' widget)* ']' ')' #rowWidget
  ;

// column rule
column
  : 'Column' '(' 'children' ':' '[' widget (',' widget)* ']' ')' #columnWidget
  ;

// text rule
text
  : 'Text' '(' STRING ')' #textWidget
  ;

// image rule
image
  : 'Image' '(' 'image' ':' 'AssetImage' '(' STRING ')' ')' #imageWidget
  ;

// list view rule
listView
  : 'ListView' '.' 'separated' '(' 'itemBuilder' ':' '(' 'context' ',' 'index' ')' '=>' column ','
    'separatorBuilder' ':' '(' 'context' ',' 'index' ')' '=>' container ','
    'itemCount' ':' INT
  ')' #listViewWidget
  ;

// container rule
container
  : 'Container' '(' 'height' ':' INT ',' 'width' ':' 'double' '.' 'infinity' ',' 'color' ':' 'Colors' '.' 'grey' ')' #containerWidget
  ;


// LEXER
NEW : 'new';
RETURN : 'return';
IF : 'if';
ELSE : 'else';
FOR : 'for';
WHILE : 'while';
CLASS : 'class';
TYPE   : 'int' | 'float' | 'bool' | 'string' | 'var' | 'char' | 'void';
STRING : '"' (' '..'~')* '"';
BOOL : 'true' | 'false';
INT    : '0'..'9'+;
FLOAT : '0'..'9'+ ('.' '0'..'9'+)?;
ID     : ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'| '0'..'9' | '_')*;
SINGLE_LINE_COMMENT : '//' ~[\r\n]* ->skip;
MULTI_LINE_COMMENT  : '/*' ( MULTI_LINE_COMMENT | .)*? '*/' ->skip;
WS     : [ \t\n\r]+ -> skip ;
AND : '&&' ;
OR : '||' ;