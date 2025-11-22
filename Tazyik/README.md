# Tazyik Language Project

## Overview

Tazyik is a declarative domain-specific language for modelling water flows in river systems with support for dams, multi-day rainfall patterns, and capacity management.

---

## Quick Start

```bash
./compile                            # Compile the project
java tazyik.Tazyik Demo_Programs/example1.txt    # Run an example
./clean                              # Clean compiled files
```

---

## Compilation

```bash
./compile
```

This will:
1. Generate AST classes using `GenerateAst.java`
2. Compile all Tazyik source files

---

## Running Programs

```bash
java tazyik.Tazyik Demo_Programs/example1.txt
java tazyik.Tazyik Demo_Programs/example2.txt
java tazyik.Tazyik Demo_Programs/example3.txt
java tazyik.Tazyik Demo_Programs/example4_dams.txt
java tazyik.Tazyik Demo_Programs/example5_dams_complex.txt
```

---

## Example Programs

1. **example1.txt** — Simple Y-shaped river system with basic flow
2. **example2.txt** — Canberra river system with two dams (demonstrates default 80% release)
3. **example3.txt** — Multi-day rainfall patterns with variable amounts
4. **example4_dams.txt** — Dam system with explicit release percentages (60% and 50%)
5. **example5_dams_complex.txt** — Complex dam example with 20% release strategy

---

## Language Features

- **Rivers**: Defined with rainfall amounts in millimeters (mm)
- **Dams**: Water storage with optional release percentage (defaults to 80%)
- **Flows**: Water movement between rivers using `->` operator
- **Capacity**: Maximum water storage limits in megalitres (ML)
- **FlowOut**: Days required for water to flow through the system (default: 3 days)
- **Multi-day rainfall**: Support for `20(3)mm` syntax and array patterns `[10,20,15]mm`

---

## Troubleshooting

- **Compilation errors**: Make sure Java JDK is installed and in your PATH
- **Class not found**: Run `./compile` before executing programs

---
