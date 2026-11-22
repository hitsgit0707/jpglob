# VoltEdge — Electrical Engineering Desktop Calculator

**VoltEdge** is an interactive desktop engineering calculation suite built in **Core Java (Java 17)** using standard **Java Swing** and **AWT**.

Designed as an academic second-year software engineering project, it emphasizes clean object-oriented architecture, modularity, defensive input validation, standard local file persistence, and thread-safe Swing GUI patterns without external dependencies or frameworks.

---

## Features & Modules

1. **Ohm's Law Calculator** ($V = I \cdot R$)
   - Calculates Voltage, Current, or Resistance from any two known parameters.
   - Enforces defensive checks against short circuits ($R = 0$) and open circuits ($I = 0$).
2. **Electrical Power Calculator** ($P = V \cdot I$, $P = I^2 \cdot R$, $P = V^2 / R$)
   - Evaluates power dissipation across circuit components using method overloading.
3. **Resistor Networks Calculator**
   - Supports series ($R_{eq} = \sum R_n$) and parallel ($\frac{1}{R_{eq}} = \sum \frac{1}{R_n}$) networks for arbitrary comma-separated resistor lists.
4. **Voltage Divider Calculator**
   - Evaluates two-resistor unloaded potential dividers: output voltage ($V_{out}$), individual resistor drops ($V_{R1}, V_{R2}$), and total divider current.
5. **Capacitor Networks Calculator**
   - Calculates parallel direct summation ($C_{eq} = \sum C_n$) and series reciprocal storage ($\frac{1}{C_{eq}} = \sum \frac{1}{C_n}$) with scientific notation parsing.
6. **Electrical Unit Converter**
   - Base-unit scaling factor conversions across metric prefixes for Voltage ($\mu\text{V} \leftrightarrow \text{kV}$), Current, Resistance, Capacitance, Power, and Frequency.
7. **Calculation History & Persistence**
   - Centralized calculation audit log with reverse-chronological `JTable` view, clear function, and fail-safe local file persistence (`voltedge_history.txt`).

---

## Core Java / OOP Concepts Demonstrated

* **Encapsulation:** Private final fields, immutable records (`HistoryRecord`), and defensive accessors.
* **Inheritance & Abstraction:** Abstract base class `CircuitCalculator` inherited by all calculation models.
* **Interfaces & Observers:** `NavigationListener` and `HistoryChangeListener` for clean, decoupled UI callbacks.
* **Method Overloading:** Overloaded calculation methods in `PowerCalculator`, constructor overloading across all UI panels.
* **Method Overriding:** Overridden `toString()`, `equals()`, and `hashCode()` in domain records.
* **Collections Framework:** `ArrayList` for dynamic component networks and calculation history logs; `LinkedHashMap` for ordered unit scale definitions.
* **File Handling:** Standard `File`, `FileReader`/`FileWriter`, `BufferedReader`/`BufferedWriter` with `try-with-resources`.
* **Swing & Concurrency:** Thread-safe execution on the Event Dispatch Thread (EDT) via `SwingUtilities.invokeLater()`, `CardLayout` screen swapping, and custom-styled components.

---

## Project Structure

```
VoltEdge/
├── app/                      # Java Desktop Application
│   ├── src/                  # Application source code
│   │   └── voltedge/
│   │       ├── Main.java     # Entry point
│   │       ├── model/        # Calculation & storage models
│   │       ├── ui/           # Swing GUI panels & theme
│   │       └── test/         # Automated test runner (RunAllTests)
│   └── bin/                  # Compiled bytecode (git-ignored)
├── docs/                     # GitHub Pages Showcase Website
│   └── index.html            # Static project information page
├── .gitignore                # Excludes binaries, IDE configs, and logs
└── README.md                 # Project technical documentation
```

---

## How to Build and Run

### Prerequisites
* Java Development Kit (JDK 17 or higher)

### 1. Compile the Application
From the repository root directory:

**Windows (PowerShell):**
```powershell
javac -d app/bin (Get-ChildItem -Recurse -Filter *.java app/src | ForEach-Object { $_.FullName })
```

**Linux / macOS (Bash):**
```bash
javac -d app/bin $(find app/src -name "*.java")
```

### 2. Run the Application
```bash
java -cp app/bin voltedge.Main
```

### 3. Run Automated Tests
```bash
java -cp app/bin voltedge.test.RunAllTests
```

---

## GitHub Pages Deployment

The static showcase site is located in `/docs`. When enabling GitHub Pages in repository settings:
* **Source:** Deploy from a branch
* **Branch:** `main`
* **Folder:** `/docs`

*Note: The GitHub Pages site serves as an online documentation showcase. VoltEdge itself runs locally as a native Java desktop application.*
