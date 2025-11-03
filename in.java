%----------------------------------------------------------------------------------------

\documentclass[a4paper]{article} % Uses article class in A4 format

%----------------------------------------------------------------------------------------
%	FORMATTING
%----------------------------------------------------------------------------------------

\addtolength{\hoffset}{-2.25cm}
\addtolength{\textwidth}{4.5cm}
\addtolength{\voffset}{-3.25cm}
\addtolength{\textheight}{5cm}
\setlength{\parskip}{0pt}
\setlength{\parindent}{0in}

%----------------------------------------------------------------------------------------
%	PACKAGES AND OTHER DOCUMENT CONFIGURATIONS
%----------------------------------------------------------------------------------------

\usepackage{blindtext} % Package to generate dummy text
% \usepackage[style=numeric,sorting=none]{biblatex}
\usepackage{charter} % Use the Charter font
\usepackage[utf8]{inputenc} % Use UTF-8 encoding
\usepackage{microtype} % Slightly tweak font spacing for aesthetics

\usepackage[english]{babel} % Language hyphenation and typographical rules

\usepackage{amsthm, amsmath, amssymb} % Mathematical typesetting
\usepackage{float} % Improved interface for floating objects
\usepackage[final, colorlinks = true, 
            linkcolor = black, 
            citecolor = black]{hyperref} % For hyperlinks in the PDF
\usepackage{graphicx, multicol} % Enhanced support for graphics
\usepackage{listings}
\usepackage{marvosym, wasysym} % More symbols
\usepackage{rotating} % Rotation tools
\usepackage{censor} % Facilities for controlling restricted text
\usepackage{listings, style/lstlisting} % Environment for non-formatted code, !uses style file!
\usepackage{pseudocode} % Environment for specifying algorithms in a natural way
\usepackage{style/avm} % Environment for f-structures, !uses style file!
\usepackage{booktabs} % Enhances quality of tables
\usepackage{tikz-qtree} % Easy tree drawing tool
\tikzset{every tree node/.style={align=center,anchor=north},
         level distance=2cm} % Configuration for q-trees
\usepackage{style/btree} % Configuration for b-trees and b+-trees, !uses style file!
% \usepackage[backend=biber,style=numeric,
            % sorting=nyt]{biblatex} % Complete reimplementation of bibliographic facilities
% \addbibresource{ecl.bib}
\usepackage{csquotes} % Context sensitive quotation facilities
%\usepackage{natbib}
\usepackage[yyyymmdd]{datetime} % Uses YEAR-MONTH-DAY format for dates
\renewcommand{\dateseparator}{-} % Sets dateseparator to '-'

\usepackage{fancyhdr} % Headers and footers
\pagestyle{fancy} % All pages have headers and footers
\fancyhead{}\renewcommand{\headrulewidth}{0pt} % Blank out the default header
%\fancyfoot[L]{School of Computing, Macquarie University} % Custom footer text
\fancyfoot[C]{} % Custom footer text
\fancyfoot[R]{\thepage} % Custom footer text

\usepackage{comment}
\newcommand{\note}[1]{\marginpar{\scriptsize \textcolor{red}{#1}}} % Enables comments in red on margin
\DeclareUnicodeCharacter{2731}{\textasteriskcentered} %This will map the Unicode character ✱ to the \textasteriskcentered symbol, which is a suitable replacement in most cases.

\usepackage[table]{xcolor}
\usepackage{tabularx}
\newcolumntype{L}{>{\raggedright\arraybackslash}X}

% define severity colours
\definecolor{InfoColor}{HTML}{ADD8E6}    % light blue
\definecolor{HighColor}{HTML}{F08080}    % light red
\definecolor{LowColor}{HTML}{FFFACD}     % light yellow
\definecolor{MediumColor}{HTML}{FFD580}  % light orange

%Coding
\lstset{
  language=Java,
  basicstyle=\ttfamily\small,
  keywordstyle=\color{blue},
  commentstyle=\color{gray},
  stringstyle=\color{red},
  showstringspaces=false,
  breaklines=true,
  frame=single
}
\usepackage{hyperref}
%----------------------------------------------------------------------------------------

% Notes: please do not forget citing

\begin{document}

%----------------------------------------------------------------------------------------
%	TITLE SECTION
%----------------------------------------------------------------------------------------

\title{COMP3000-Programming-Languages-A1} % Article title
\fancyhead[C]{}
\hrule \medskip % Upper rule
\begin{minipage}{1\textwidth} % Center of title section
\centering 
\large % Title text size
COMP3000 - Programming Languages
Assignment 1 - Domain specific language (DSL) for the modelling and simulation of water flows
in river systems, S2, 2025\\
\normalsize % Subtitle text size
SID: 47938234, Name: Ibrahim Omeroglu\\
%%%%\\ % Assignment subtitle
\end{minipage}
\medskip\hrule % Lower rule
\bigskip

%----------------------------------------------------------------------------------------
%	ARTICLE CONTENTS
%----------------------------------------------------------------------------------------

\section*{Tazyik Language Design Documentation}

\subsection*{Introduction}

This document presents the design and implementation of Tazyik, a domain-specific language for modelling water flows in river systems. The name "Tazyik" derives from the Persian word meaning "pressure,". The language provides intuitive syntax for defining rivers, rainfall patterns, flow connections, and capacity constraints, enabling comprehensive simulation of complex watershed systems.

\subsection*{Assignment Table A}

\begin{table}[H]
\centering
\begin{tabular}{|l|l|}
\hline
\textbf{Question} & \textbf{Answer} \\
\hline
River literal representation (1 day) & \texttt{12mm} \\
\hline
River combination symbol & \texttt{+} \\
\hline
Symbol type & Binary operator \\
\hline
Working folder & \texttt{tazyik} \\
\hline
Compilation command & \texttt{javac tazyik/*.java} \\
\hline
Water flow time through a river system & 3 days (default) or define at least 4 days specific FlowOut \\
\hline
Language type & Statement-based language \\
\hline
Starting book chapter & Chapter 4 (Scanning) \\
\hline
\end{tabular}
\caption{Assignment specification requirements}
\end{table}

\subsection*{Language Syntax and Usage}

The Tazyik language follows a declarative approach where users define system components and their relationships. The typical program structure follows these patterns:

\begin{enumerate}
\item Define water movement timing through the system (optional, defaults to 3 days):
\begin{lstlisting}
FlowOut flowOut = 5; // Water takes 5 days to flow through system
\end{lstlisting}

\item Define individual rivers with rainfall amounts:
\begin{lstlisting}
River googong = 12mm; // 12mm rainfall one day on this river
River jerra = 14mm; 
River molonglo = 45mm;
\end{lstlisting}

\item Specify multi-day rainfall patterns:
\begin{lstlisting}
River googong = 12(2)mm; // 12mm rainfall for days 1 and 2
River jerra = 14(5)mm; 
River molonglo = 45(3)mm; 
      googong = 0(2)mm; // update with no rain days 3-4
      googong = 8(1)mm; // update rain day 5

// Alternative variable rainfall syntax
River jerra = [14,12,8,5,3]mm; // Different amounts for 5 days
\end{lstlisting}

\item Define flow connections between rivers:
\begin{lstlisting}
Flow system = (googong + jerra) -> molonglo;
\end{lstlisting}

\item Define river capacities in megalitres:
\begin{lstlisting}
Capacity googong = 200ML; // Maximum capacity
Capacity jerra = 150ML; 
Capacity molonglo = 500ML;
\end{lstlisting}
\end{enumerate}

\subsection*{Language Semantics}

The Tazyik language operates under several key semantic principles:

\begin{itemize}
\item Rivers receive rainfall measured in millimetres (mm)
\item Rivers support multi-day rainfall specified with (days) syntax and variable amounts per day
\item River states can be updated to add additional rainfall periods
\item Rivers connect using the '+' operator for combination and -\verb|>| for flow direction
\item Water takes 3 days to flow completely through the system by default or should define more days
\item Flow connections define water movement through the river network
\item Capacity constraints in megalitres (ML) trigger different behaviours when exceeded
\end{itemize}

\subsubsection*{FlowOut Semantics}

The \texttt{FlowOut} declaration defines the number of days required for rainfall to move completely through the river system. This affects flow calculations through:

\begin{enumerate}
\item \textbf{Water Distribution}: Rain falling on day N distributes equally over the next \texttt{FlowOut} days
\item \textbf{Default Value}: Three days serves as the default when unspecified
\item \textbf{Declaration Order}: FlowOut should precede river declarations for proper multi-day rainfall interpretation
\end{enumerate}

For example, with \texttt{FlowOut flowOut = 5;} and 10mm rainfall on day 1, each subsequent day receives 2mm (10mm ÷ 5 days) of flow contribution.

\subsubsection*{Capacity Management}

When river capacities reach certain thresholds, the system triggers specific responses:

\begin{itemize}
\item At 80\% capacity: System generates warnings
\item At 100\% capacity: Spillage occurs (approximately 5\% overflow to land)
\item Beyond 100\%: Flooding affects the flow system with 10\% increases to affected areas
\end{itemize}

\subsection*{Formal Grammar}

The Tazyik language follows this formal grammar structure:

\begin{lstlisting}
program            → flowOutDecl? otherDeclarations EOF ;

otherDeclarations  → flowOutDecl | riverDecl | riverUpdate | 
                     flowDecl | capacityDecl;

riverDecl          → "River" IDENTIFIER "=" expression ";" ;
flowDecl           → "Flow" IDENTIFIER "=" expression ";" ;
capacityDecl       → "Capacity" IDENTIFIER "=" NUMBER "ML" ";" ;
flowOutDecl        → "FlowOut" IDENTIFIER "=" NUMBER ";";
riverUpdate        → IDENTIFIER "=" expression ";" ;

expression         → flowExpr ;
flowExpr           → addExpr ( "->" addExpr )* ;
addExpr            → primary ( "+" primary )* ;
primary            → IDENTIFIER | NUMBER ("(" NUMBER ")")? "mm" | 
                     "[" NUMBER ("," NUMBER)* "]" "mm" | 
                     "(" expression ")" ;
\end{lstlisting}

The grammar employs optional syntax (?) to allow FlowOut declarations while requiring their placement at program start when present.

\subsection*{Parsing Examples}

This section demonstrates how each statement in the three example programs maps to the formal grammar rules, showing the complete syntactic structure of the Tazyik language.

\subsubsection*{Example 1: Simple River System}

The program structure for this example includes:

\begin{lstlisting}
River upstream = 10mm;
River midStream = 8mm;
River downstream = 5mm;
Flow mainSystem = (upstream + midStream) -> downstream;
Capacity upstream = 50ML;
Capacity midStream = 30ML;
Capacity downstream = 100ML;
\end{lstlisting}

\textbf{1.1 River Declaration: \texttt{River upstream = 10mm;}}

This statement demonstrates basic river declaration parsing following the \texttt{riverDecl} grammar rule.

\begin{figure}[H]
\centering
\includegraphics[width=0.8\textwidth]{parsing_example_1_1.png}
\caption{Parsing tree for river declaration \texttt{River upstream = 10mm;}}
\end{figure}

\textbf{1.2 Flow Declaration: \texttt{Flow mainSystem = (upstream + midStream) -> downstream;}}

This statement illustrates complex flow declaration parsing with parenthesized expressions and the flow direction operator.

\begin{figure}[H]
\centering
\includegraphics[width=1.0\textwidth]{parsing_example_1_2.png}
\caption{Parsing tree for flow declaration with combination and direction}
\end{figure}

\textbf{1.3 Capacity Declaration: \texttt{Capacity upstream = 50ML;}}

This statement shows capacity declaration parsing following the \texttt{capacityDecl} grammar rule.

\begin{figure}[H]
\centering
\includegraphics[width=0.8\textwidth]{parsing_example_1_3.png}
\caption{Parsing tree for capacity declaration}
\end{figure}

\subsubsection*{Example 2: Canberra River System}

The key statements from this program structure include:

\begin{lstlisting}
FlowOut flowOut = 5;
River googong = 15mm;
Flow googongFlow = googong -> dam1;
Flow combineFlow = (queanbeyan + upperMolonglo + jerrabomberra) 
                   -> centralMolonglo;
Flow outputFlow = dam2 -> lowerMolonglo;
\end{lstlisting}

\textbf{2.1 FlowOut Declaration: \texttt{FlowOut flowOut = 5;}}

This statement demonstrates FlowOut declaration parsing following the \texttt{flowOutDecl} grammar rule.

\begin{figure}[H]
\centering
\includegraphics[width=0.8\textwidth]{parsing_example_2_1.png}
\caption{Parsing tree for FlowOut declaration}
\end{figure}

\textbf{2.2 Simple Flow: \texttt{Flow googongFlow = googong -> dam1;}}

This statement illustrates basic flow connection parsing with the flow direction operator.

\begin{figure}[H]
\centering
\includegraphics[width=0.9\textwidth]{parsing_example_2_2.png}
\caption{Parsing tree for simple flow connection}
\end{figure}

\textbf{2.3 Complex Combination: \texttt{Flow combineFlow = (queanbeyan + upperMolonglo + jerrabomberra) -> centralMolonglo;}}

This statement demonstrates parsing of complex river combinations with multiple addition operations within parentheses.

\begin{figure}[H]
\centering
\includegraphics[width=1.0\textwidth]{parsing_example_2_3.png}
\caption{Parsing tree for complex river combination flow}
\end{figure}

\textbf{2.4 Final Output Flow: \texttt{Flow outputFlow = dam2 -> lowerMolonglo;}}

This statement shows the parsing of the final system output connection, completing the river network topology.

\begin{figure}[H]
\centering
\includegraphics[width=0.9\textwidth]{parsing_example_2_4.png}
\caption{Parsing tree for final output flow connection}
\end{figure}

\subsubsection*{Example 3: Multi-day Rainfall System}

The key statements from this program structure include:

\begin{lstlisting}
River mountain = 20(3)mm;
River valley = [14,12,8,5,3]mm;
River plain = 0(2)mm;
      plain = 10(1)mm;
Flow lowerSystem = (valley + plain) -> (plain1 + plain2) -> outlet;
\end{lstlisting}

\textbf{3.1 Multi-day Rainfall: \texttt{River mountain = 20(3)mm;}}

This statement demonstrates parsing of multi-day rainfall syntax with parenthetical day specification.

\begin{figure}[H]
\centering
\includegraphics[width=0.9\textwidth]{parsing_example_3_1.png}
\caption{Parsing tree for multi-day rainfall declaration}
\end{figure}

\textbf{3.2 Array Rainfall: \texttt{River valley = [14,12,8,5,3]mm;}}

This statement illustrates parsing of variable rainfall amounts using array literal syntax.

\begin{figure}[H]
\centering
\includegraphics[width=1.0\textwidth]{parsing_example_3_2.png}
\caption{Parsing tree for array rainfall declaration}
\end{figure}

\textbf{3.3 River Update: \texttt{plain = 10(1)mm;}}

This statement shows parsing of river update syntax following the \texttt{riverUpdate} grammar rule.

\begin{figure}[H]
\centering
\includegraphics[width=0.9\textwidth]{parsing_example_3_3.png}
\caption{Parsing tree for river update statement}
\end{figure}

\textbf{3.4 Multi-stage Flow: \texttt{Flow lowerSystem = (valley + plain) -> (plain1 + plain2) -> outlet;}}

This statement demonstrates parsing of multi-stage flow connections with multiple flow direction operators and parenthesized combinations.

\begin{figure}[H]
\centering
\includegraphics[width=1.0\textwidth]{parsing_example_3_4.png}
\caption{Parsing tree for multi-stage flow with multiple direction operators}
\end{figure}

\subsubsection*{Grammar Rule Mapping}

Each parsing tree demonstrates adherence to the formal grammar rules:

\begin{enumerate}
\item \textbf{program} → flowOutDecl? otherDeclarations EOF
\item \textbf{otherDeclarations} → flowOutDecl | riverDecl | riverUpdate | flowDecl | capacityDecl  
\item \textbf{riverDecl} → "River" IDENTIFIER "=" expression ";"
\item \textbf{flowDecl} → "Flow" IDENTIFIER "=" expression ";"
\item \textbf{capacityDecl} → "Capacity" IDENTIFIER "=" NUMBER "ML" ";"
\item \textbf{flowOutDecl} → "FlowOut" IDENTIFIER "=" NUMBER ";"
\item \textbf{riverUpdate} → IDENTIFIER "=" expression ";"
\item \textbf{expression} → flowExpr
\item \textbf{flowExpr} → addExpr ( "->" addExpr )*
\item \textbf{addExpr} → primary ( "+" primary )*
\item \textbf{primary} → IDENTIFIER | NUMBER ("(" NUMBER ")")? "mm" | "[" NUMBER ("," NUMBER)* "]" "mm" | "(" expression ")"
\end{enumerate}

The parsing trees illustrate how each statement type maps to specific grammar productions, demonstrating the parser's ability to handle complex nested expressions while maintaining clear syntactic structure. The precedence relationships between operators ensure correct interpretation of flow direction and river combination operations, supporting the language's intended semantics for hydrological system modelling.

\subsection*{My Example Programs}

This section describes the three example programs submitted and their corresponding river systems.

\subsubsection*{Example 1: Simple River System}

This program models a basic Y-shaped watershed where two upstream rivers merge into a single downstream channel. The system demonstrates fundamental Tazyik concepts including river declarations, flow combinations, and capacity management.

\textbf{River System Structure:}
\begin{itemize}
\item Upstream river: 10mm rainfall, 50ML capacity
\item MidStream river: 8mm rainfall, 30ML capacity  
\item Downstream river: 5mm rainfall, 100ML capacity
\item Flow pattern: (upstream + midStream) → downstream
\end{itemize}

This example illustrates basic river combination using parentheses and the binary flow operator, representing common watershed configurations found in mountainous regions.

\subsubsection*{Example 2: Canberra River System}

This program replicates the actual Canberra river network as described in the assignment specification. The system includes multiple tributaries, two major dams, and complex flow routing through the Molonglo River system.

\textbf{River System Structure:}
\begin{itemize}
\item Root rivers: Googong (15mm), Jerrabomberra (12mm), Upper Molonglo (18mm)
\item Infrastructure: Dam1 (500ML), Dam2 (1200ML)
\item Flow sequence: Googong → Dam1 → Queanbeyan
\item Convergence: (Queanbeyan + Upper Molonglo + Jerrabomberra) → Central Molonglo
\item Final routing: Central Molonglo → Dam2 → Lower Molonglo
\end{itemize}

This example demonstrates complex multi-stage flow networks with infrastructure components, showcasing Tazyik's ability to model real-world hydrological systems with multiple convergence points and storage facilities.

\subsubsection*{Example 3: Multi-day Rainfall System}

This program showcases Tazyik's temporal modelling capabilities through varied rainfall patterns across multiple days. The system demonstrates both consistent and variable rainfall syntax options.

\textbf{River System Structure:}
\begin{itemize}
\item Mountain river: 20mm for 3 consecutive days
\item Valley river: Variable rainfall [14,12,8,5,3]mm over 5 days
\item Plain river: No rain for 2 days, then 10mm on day 3
\item Multiple tributaries: Plain1 (5mm for 2 days), Plain2 (3mm for 4 days)
\item Flow stages: Mountain → Valley, then (Valley + Plain) → (Plain1 + Plain2) → Outlet
\end{itemize}

This example illustrates Tazyik's flexibility in handling complex temporal patterns and multi-stage flow networks, representing dynamic weather systems affecting watershed behaviour over extended periods.

\subsection*{How to Compile and Run}

The Tazyik parser compilation follows a standard Java development workflow with specific steps (Just adjust file paths to match your OS) for AST generation:


\begin{enumerate}
\item \textbf{AST Generation:} Generate the abstract syntax tree classes
\begin{lstlisting}
$ javac tazyik/tool/GenerateAst.java
$ java -cp tazyik/tool/ GenerateAst tazyik/source/
\end{lstlisting}

\item \textbf{Parser Compilation:} Compile all source files
\begin{lstlisting}
$ javac tazyik/source/*.java
\end{lstlisting}

\item \textbf{Program Execution:} Run example programs (when interpreter implementation completes)
\begin{lstlisting}
$ java tazyik.source.Tazyik tazyik/source/example1.txt
\end{lstlisting}
\end{enumerate}

The compilation process generates necessary AST node classes before compiling the main parser components, following the Crafting Interpreters methodology for systematic language implementation.

\subsection*{How I Created My Language}

The Tazyik language design reflects careful consideration of domain-specific requirements and user experience factors. Each design choice addresses specific challenges in hydrological modelling while maintaining language simplicity and expressiveness.

\subsubsection*{Design Choices with Reasoning}

\textbf{1. Different Units (mm for rainfall, ML for capacity)}

The language employs distinct units reflecting real-world domain concepts. Rainfall measurements use millimetres matching meteorological standards, while reservoir capacity uses megalitres following water management conventions. This design creates clear semantic distinction between different value types, prevents accidental mixing of incompatible quantities, and enables the parser to distinguish them through different token types.

\textbf{2. Named Rivers vs. Array Indices}

River identification uses meaningful names rather than numeric indices. This approach proves more intuitive and self-documenting since rivers possess names in real-world contexts. Named rivers simplify maintenance as adding or removing rivers avoids index shifting, improves code readability and understanding, and matches the problem domain more naturally than abstract numbering systems.

\textbf{3. Statement-Based Language Structure}

The language adopts statement-based syntax rather than expression-based design. This choice clearly separates declarations of rivers, flows, and capacities, provides sequential execution models compared to expression-based alternatives, makes the language accessible to non-programmers, and follows patterns established in configuration and modelling languages.

\textbf{4. River Update Syntax}

River modification uses update syntax allowing changes to existing rivers without complete redefinition. This design supports modelling of changing rainfall patterns over time, reduces redundancy in multi-day simulations, and follows natural language concepts of modifying existing entities rather than creating new ones.

\textbf{5. Multi-Day Rainfall Options}

The language provides two syntaxes addressing different rainfall modelling needs. Simple repeating syntax (12(2)mm) handles consistent rainfall periods, while variable array syntax ([14,12,8,5,3]mm) accommodates changing patterns. This dual approach balances simplicity with expressiveness and accommodates diverse modelling requirements.

\textbf{6. Flow Direction Operator (-\texttt{>})}

The arrow operator visually indicates water movement direction through intuitive representation of physical flow processes. This design distinguishes flow direction from combination operations (+) and follows established conventions for "source to destination" representation in technical documentation.

\textbf{7. FlowOut Semantic Model}

Water distribution follows equal allocation across specified days with default three-day periods based on typical river system behaviour. This linear distribution model balances accuracy with computational simplicity while remaining easy to implement and understand.

\textbf{8. Capacity Threshold Behaviours}

The system employs progressive response mechanisms with warnings at 80\% capacity and spillage at 100\%. This design models real-world water management practices, provides clear thresholds for different events, and supports multiple outcomes from single conditions.

\textbf{9. Grammar Structure with Clear Precedence}

Flow direction (-\texttt{>}) operates at lower precedence than combination (+), allowing natural expression parsing. Parentheses enable explicit grouping to override precedence rules. The grammar follows natural reading order of expressions while supporting complex network topologies and maintaining readability.

\subsection*{Changes Compared to Nystrom}

The Tazyik parser extends the Crafting Interpreters foundation with several domain-specific modifications and enhancements.

\subsubsection*{Lexical Analysis Modifications}

The scanner incorporates new token types absent from the original Lox implementation:

\begin{itemize}
\item \texttt{MM} tokens for millimetre rainfall units
\item \texttt{ML} tokens for megalitres capacity units  
\item \texttt{ARROW} tokens for flow direction operator (->)
\item \texttt{RIVER}, \texttt{FLOW}, \texttt{CAPACITY}, \texttt{FLOWOUT} keyword tokens
\item Number parsing extended to handle parenthetical day specifications
\item Array literal scanning for variable rainfall patterns
\end{itemize}

\subsubsection*{Grammar Structure Extensions}

The parser grammar diverges significantly from Lox's expression-based approach:

\begin{itemize}
\item Statement-based top-level structure replacing expression evaluation
\item Declaration-focused grammar supporting multiple statement types
\item Custom precedence rules for flow operators distinct from arithmetic operations
\item Optional FlowOut declarations with positional constraints
\item River update syntax enabling state modifications
\end{itemize}

\subsubsection*{AST Node Additions}

New abstract syntax tree nodes support domain-specific constructs:

\begin{itemize}
\item \texttt{RiverDecl} nodes for river declarations with rainfall specifications
\item \texttt{FlowDecl} nodes for flow connection definitions
\item \texttt{CapacityDecl} nodes for capacity limit specifications
\item \texttt{FlowOutDecl} nodes for system timing configuration
\item \texttt{RiverUpdate} nodes for modifying existing river states
\item Enhanced \texttt{Primary} expressions supporting unit suffixes and array literals
\end{itemize}

\subsubsection*{Parsing Strategy Adaptations}

The recursive descent parser employs modified strategies:

\begin{itemize}
\item Declaration parsing replaces statement parsing as primary program structure
\item Expression parsing handles flow direction operators with custom precedence
\item Error recovery mechanisms adapted for declaration-heavy syntax
\item Token consumption patterns modified for unit-suffixed literals
\end{itemize}

\subsubsection*{Semantic Analysis Considerations}

While Lox focuses on dynamic typing and runtime evaluation, Tazyik incorporates domain-specific semantic requirements:

\begin{itemize}
\item Unit type checking between mm and ML values
\item Flow network validation for connectivity and cycles  
\item Rainfall pattern consistency verification
\item Capacity constraint logical validation
\end{itemize}

\subsection*{Team Operation}

The team collaboration contributed significantly to the language design and implementation process, though individual extensions enhanced the final submission.

\subsubsection*{Team Collaboration Process}

Team discussions focused on understanding assignment requirements and exploring implementation approaches. Members shared interpretations of the specification, particularly regarding grammar design choices and semantic requirements. The team examined various syntax options for river declarations and flow connections through collaborative analysis of domain requirements.

Regular meetings addressed parsing challenges, especially concerning precedence rules for flow operators and handling of multi-day rainfall syntax. Team members contributed different perspectives on user experience considerations, helping refine the language's accessibility for non-programmers.

\subsubsection*{Shared Development Activities}

The team worked together on fundamental parser structure decisions, including token design for domain-specific units and basic grammar rule formulation. Collaborative debugging sessions helped resolve parsing conflicts and precedence issues during initial implementation phases.

Team members shared research on real-world hydrological systems, informing semantic choices about water flow timing and capacity management. This collective domain knowledge improved the language's practical applicability and realism.

\subsubsection*{Individual Extensions}

Building upon team foundations, individual work focused on several enhancement areas:

\begin{itemize}
\item Enhanced grammar formalization with comprehensive rule documentation  
\item Expanded example programs demonstrating complex multi-stage flow networks
\item Detailed parsing tree visualizations showing syntactic analysis processes
\item Comprehensive semantic specification for FlowOut behaviour and capacity thresholds
\item Extended error handling mechanisms for domain-specific validation requirements
\end{itemize}

The individual contribution emphasized documentation quality and implementation completeness, ensuring the parser fully supports all specified language features while maintaining clear explanation of design decisions and parsing processes.

\subsubsection*{Learning Outcomes}

Team collaboration enhanced understanding of language design trade-offs and implementation challenges. Individual extension work deepened appreciation for documentation requirements and user experience considerations in domain-specific language development.

The combination of team discussion and individual refinement produced a more robust and well-documented language implementation than either approach alone could have achieved.

% Note: Mermaid parsing diagrams would be inserted here in the final document
% Converting the existing mermaid graphs to tikz-qtree format would maintain
% the visual parsing demonstrations while conforming to LaTeX requirements

\subsection*{Conclusion}

The Tazyik language successfully addresses the assignment requirements through thoughtful design choices that balance domain specificity with implementation practicality. The statement-based syntax, intuitive operators, and comprehensive grammar support complex hydrological modelling while remaining accessible to users without extensive programming backgrounds.

The parser implementation extends Crafting Interpreters foundations with domain-specific enhancements that demonstrate understanding of both language design principles and practical implementation requirements. The comprehensive documentation and example programs provide clear guidance for marker evaluation and future development activities.
%----------------------------------------------------------------------------------------
%	REFERENCE LIST
%----------------------------------------------------------------------------------------
 %\bibliographystyle{ieeetr}
 %\bibliography{comp3100ass1}
% \printbibliography

%-----------
\end{document}
