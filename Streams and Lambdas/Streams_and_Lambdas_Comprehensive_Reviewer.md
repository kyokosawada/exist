# Java Streams and Lambdas: Comprehensive Reviewer

## Table of Contents

1. [Lambda Expressions](#1-lambda-expressions)
2. [Functional Interfaces](#2-functional-interfaces)
3. [Method References](#3-method-references)
4. [Stream API](#4-stream-api)
5. [Parallel Streams](#5-parallel-streams)
6. [Optional](#6-optional)
7. [Best Practices and Performance](#7-best-practices-and-performance)

---

## 1. Lambda Expressions

### 1.1 Introduction to Lambda Expressions

**Definition**: Lambda expressions are anonymous functions that provide a clear and concise way to
represent one method interface using an expression. Introduced in Java 8, they enable functional
programming paradigms in Java.

**Book References**:

- *Java: The Complete Reference* by Herbert Schildt (Chapter 15)
- *Effective Java* by Joshua Bloch (Items 42-44)
- *Java 8 in Action* by Raoul-Gabriel Urma, Mario Fusco, Alan Mycroft

### 1.2 Lambda Syntax

**Basic Syntax**:

```
(parameters) -> expression
(parameters) -> { statements; }
```

**Components**:

1. **Parameter List**: Zero or more parameters enclosed in parentheses
2. **Arrow Token**: `->` separates parameters from body
3. **Body**: Single expression or block of statements

**Examples**:

```java
// No parameters
() -> System.out.println("Hello World")

// Single parameter (parentheses optional)
x -> x * x
(x) -> x * x

// Multiple parameters
(x, y) -> x + y

// Block body
(x, y) -> {
    int sum = x + y;
    return sum;
}

// Type inference (Java infers types)
(String s) -> s.length()  // Explicit type
s -> s.length()           // Inferred type
```

### 1.3 Lambda vs Anonymous Classes - Detailed Comparison

Both Lambda expressions and Anonymous classes create implementations of interfaces on-the-fly, but
they have significant differences:

#### 1.3.1 Syntax Comparison

**Anonymous Class Syntax**:
```java
// Traditional Anonymous Class - Verbose (5+ lines)
Runnable task = new Runnable() {
    @Override
    public void run() {
        System.out.println("Running task");
    }
};

// Comparator example
Comparator<String> comp = new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
};

// Event handling example
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Button clicked!");
        updateUI();
    }
});
```

**Lambda Expression Syntax**:

```java
// Lambda Expression - Concise (1 line)
Runnable task = () -> System.out.println("Running task");

// Comparator example
Comparator<String> comp = (a, b) -> a.compareTo(b);
// Or even simpler with method reference
Comparator<String> comp = String::compareTo;

// Event handling example
button.addActionListener(e -> {
    System.out.println("Button clicked!");
    updateUI();
});
// Even more concise for simple actions
button.addActionListener(e -> updateUI());
```

**Key Difference**: Lambdas eliminate boilerplate code dramatically - from 5+ lines to 1 line!

#### 1.3.2 Interface Requirements

**Anonymous Classes**:

- Can implement **any interface** (functional or not)
- Can extend **abstract classes**
- Can implement interfaces with **multiple abstract methods**

```java
// Can implement interface with multiple methods
interface MultiMethodInterface {
    void method1();
    void method2();
    default void method3() { }
}

MultiMethodInterface impl = new MultiMethodInterface() {
    @Override
    public void method1() { /* implementation */ }
    
    @Override
    public void method2() { /* implementation */ }
};

// Can extend abstract classes
Thread thread = new Thread() {
    @Override
    public void run() {
        // thread logic
    }
};
```

**Lambda Expressions**:

- **ONLY work with functional interfaces** (interfaces with exactly one abstract method)
- **Cannot extend classes**
- Must target functional interfaces

```java
// ✅ WORKS - Runnable is functional interface (one abstract method: run())
Runnable task = () -> System.out.println("Task");

// ✅ WORKS - Comparator is functional interface (one abstract method: compare())
Comparator<String> comp = (a, b) -> a.compareTo(b);

// ❌ WON'T WORK - Interface with multiple abstract methods
// MultiMethodInterface multi = () -> { }; // COMPILATION ERROR
```

#### 1.3.3 Performance Differences

**Anonymous Classes**:

- Creates a **new .class file** at compile time (MyClass$1.class, MyClass$2.class, etc.)
- **Higher memory overhead** (each instance creates new object)
- Uses traditional **method invocation**

**Lambda Expressions**:

- **No new .class files** created
- Uses **invokedynamic** (more efficient)
- **Better performance** and lower memory footprint (typically 2-3x faster)
- JVM can optimize lambda calls better

**Performance Benchmark Example**:

```java
public class PerformanceTest {
    private static final int ITERATIONS = 1_000_000;
    
    public static void main(String[] args) {
        // Anonymous class timing
        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            Runnable anonymous = new Runnable() {
                public void run() { /* do nothing */ }
            };
        }
        long anonymousTime = System.nanoTime() - start;
        
        // Lambda timing
        start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            Runnable lambda = () -> { /* do nothing */ };
        }
        long lambdaTime = System.nanoTime() - start;
        
        System.out.println("Anonymous: " + anonymousTime / 1_000_000 + "ms");
        System.out.println("Lambda: " + lambdaTime / 1_000_000 + "ms");
        // Lambda is typically 2-3x faster
    }
}
```

#### 1.3.4 Variable Scope and Capture - Detailed Explanation

**Variable Scope and Capture** is one of the most important differences between anonymous classes
and lambdas. Let's break this down completely:

## **What is Variable Capture?**

**Variable Capture** = When an anonymous class or lambda "remembers" and uses variables from the
surrounding scope.

```java
public void example() {
    String message = "Hello World"; // This is a LOCAL variable
    
    // Both anonymous class and lambda can "capture" this variable
    Runnable anonymous = new Runnable() {
        public void run() {
            System.out.println(message); // CAPTURING the local variable
        }
    };
    
    Runnable lambda = () -> System.out.println(message); // CAPTURING the local variable
}
```

## **Anonymous Classes - Own Scope**

Anonymous classes create their **own separate scope** - like having their own "room" with their own
variables.

```java
public class OuterClass {
    private String outerField = "outer";
    
    public void demonstrateAnonymous() {
        String localVar = "local";
        
        Runnable anonymous = new Runnable() {
            private String innerField = "inner"; // ✅ Can have instance variables
            private String anotherVar = "another"; // ✅ Can have multiple variables
            
            @Override
            public void run() {
                // Can access ALL these different scopes:
                System.out.println(outerField);    // ✅ Outer class field
                System.out.println(localVar);     // ✅ Local variable (effectively final)
                System.out.println(innerField);   // ✅ Anonymous class field
                System.out.println(anotherVar);   // ✅ Another anonymous class field
                System.out.println(this.innerField); // ✅ Explicit 'this' reference
                
                // Can even have methods!
                helper();
            }
            
            // ✅ Anonymous classes can have their own methods
            private void helper() {
                System.out.println("Helper method in anonymous class");
                System.out.println(this.innerField); // Access own fields
            }
        };
    }
}
```

### **Anonymous Class Scope Layers:**

```java
public class ScopeExample {
    private String classField = "Class Level";

    public void method() {
        String methodVar = "Method Level";

        Runnable anonymous = new Runnable() {
            private String anonymousField = "Anonymous Level";

            @Override
            public void run() {
                // Layer 1: Anonymous class scope
                System.out.println(anonymousField);     // "Anonymous Level"
                System.out.println(this.anonymousField); // "Anonymous Level"

                // Layer 2: Method scope  
                System.out.println(methodVar);          // "Method Level"

                // Layer 3: Class scope
                System.out.println(classField);         // "Class Level"
                System.out.println(ScopeExample.this.classField); // "Class Level"
            }
        };
    }
}
```

## **Lambda Expressions - Enclosing Scope**

Lambdas **do NOT create their own scope** - they share the same scope as the method they're written
in.

```java
public class OuterClass {
    private String outerField = "outer";
    
    public void demonstrateLambda() {
        String localVar = "local";
        
        Runnable lambda = () -> {
            // ❌ CANNOT have instance variables like anonymous classes
            // private String innerField = "inner"; // COMPILATION ERROR!
            
            // Can only access variables from enclosing scopes:
            System.out.println(outerField);    // ✅ Outer class field
            System.out.println(localVar);     // ✅ Local variable (effectively final)
            System.out.println(this.outerField); // ✅ 'this' refers to OuterClass
            
            // Local variables are OK (not instance variables)
            String tempVar = "temporary"; // ✅ This is just a local variable
            System.out.println(tempVar);
        };
    }
}
```

### **Lambda Scope Limitation:**

```java
public void lambdaLimitations() {
    String localVar = "local";
    
    Runnable lambda = () -> {
        // ✅ Can create local variables
        String temp = "temporary";
        int counter = 0;
        
        // ❌ CANNOT create instance fields
        // private String field = "field"; // COMPILATION ERROR!
        
        // ❌ CANNOT create methods
        // private void helper() { } // COMPILATION ERROR!
        
        System.out.println(localVar);
        System.out.println(temp);
    };
}
```

## **The "Effectively Final" Rule**

Both anonymous classes and lambdas can only capture variables that are **effectively final**.

### **What is "Effectively Final"?**

**Effectively Final** = A variable that is never modified after it's initialized, even if it's not
declared as `final`.

```java
public void effectivelyFinalExamples() {
    // ✅ Effectively final - never changes after initialization
    String message = "Hello";
    int count = 5;
    
    // ❌ NOT effectively final - value changes
    int counter = 0;
    counter++; // This makes it NOT effectively final
    
    // Lambda can capture effectively final variables
    Runnable lambda1 = () -> {
        System.out.println(message); // ✅ OK
        System.out.println(count);   // ✅ OK
        // System.out.println(counter); // ❌ COMPILATION ERROR!
    };
    
    // Anonymous class has same rule
    Runnable anonymous = new Runnable() {
        public void run() {
            System.out.println(message); // ✅ OK
            System.out.println(count);   // ✅ OK
            // System.out.println(counter); // ❌ COMPILATION ERROR!
        }
    };
}
```

### **Why the "Effectively Final" Rule?**

```java
public void whyEffectivelyFinal() {
    int counter = 0;
    
    // If this were allowed (it's not):
    Runnable lambda = () -> {
        System.out.println(counter); // Which value? 0? 5? 10?
    };
    
    counter = 5;  // Value changes
    lambda.run(); // What should this print?
    
    counter = 10; // Value changes again
    lambda.run(); // What should this print?
    
    // The rule prevents this confusion!
}
```

### **Workarounds for Effectively Final:**

```java
public void effectivelyFinalWorkarounds() {
    // ❌ This doesn't work:
    int counter = 0;
    Runnable lambda = () -> {
        // counter++; // COMPILATION ERROR!
    };
    
    // ✅ Workaround 1: Use wrapper objects
    AtomicInteger atomicCounter = new AtomicInteger(0);
    Runnable lambda1 = () -> {
        atomicCounter.incrementAndGet(); // ✅ OK - object reference is final
    };
    
    // ✅ Workaround 2: Use arrays
    int[] counterArray = {0};
    Runnable lambda2 = () -> {
        counterArray[0]++; // ✅ OK - array reference is final
    };
    
    // ✅ Workaround 3: Use final variables
    final int finalCounter = counter;
    Runnable lambda3 = () -> {
        System.out.println(finalCounter); // ✅ OK
    };
}
```

## **`this` Reference Behavior - The Big Difference**

This is one of the **most important** differences between anonymous classes and lambdas:

### **Anonymous Classes - `this` refers to Anonymous Instance**

```java
public class ThisExample {
    private String name = "OuterClass";
    
    public void demonstrateAnonymousThis() {
        Runnable anonymous = new Runnable() {
            private String name = "AnonymousClass"; // Same field name!
            
            @Override
            public void run() {
                // 'this' refers to the ANONYMOUS CLASS instance
                System.out.println(this.name);              // "AnonymousClass"
                System.out.println(name);                   // "AnonymousClass"
                
                // To access outer class, use OuterClass.this
                System.out.println(ThisExample.this.name);  // "OuterClass"
                
                // Demonstrate different 'this' references
                System.out.println(this.getClass().getName()); // Something like "ThisExample$1"
                System.out.println(ThisExample.this.getClass().getName()); // "ThisExample"
            }
        };
    }
}
```

### **Lambda Expressions - `this` refers to Enclosing Instance**

```java
public class ThisExample {
    private String name = "OuterClass";
    
    public void demonstrateLambdaThis() {
        Runnable lambda = () -> {
            // 'this' refers to the ENCLOSING CLASS instance (ThisExample)
            System.out.println(this.name);  // "OuterClass"
            System.out.println(name);       // "OuterClass"
            
            // No separate 'this' for lambda - it shares the enclosing 'this'
            System.out.println(this.getClass().getName()); // "ThisExample"
            
            // Cannot create fields with same name as outer class
            // String name = "LocalVar"; // This would shadow the field, not create a field
        };
    }
}
```

### **Practical `this` Example:**

```java
public class ButtonHandler {
    private String handlerName = "MainHandler";
    private int clickCount = 0;
    
    public void setupAnonymousHandler(Button button) {
        button.addActionListener(new ActionListener() {
            private int clickCount = 0; // Different from outer clickCount!
            
            @Override
            public void actionPerformed(ActionEvent e) {
                this.clickCount++; // Increments ANONYMOUS class clickCount
                System.out.println("Anonymous clicks: " + this.clickCount);
                System.out.println("Handler: " + ButtonHandler.this.handlerName);
                
                // To access outer clickCount:
                ButtonHandler.this.clickCount++;
                System.out.println("Outer clicks: " + ButtonHandler.this.clickCount);
            }
        });
    }
    
    public void setupLambdaHandler(Button button) {
        button.addActionListener(e -> {
            this.clickCount++; // Increments OUTER class clickCount directly
            System.out.println("Lambda clicks: " + this.clickCount);
            System.out.println("Handler: " + this.handlerName);
            
            // No separate scope - everything refers to enclosing class
        });
    }
}
```

## **Instance Variables Comparison**

### **Anonymous Classes Can Have Instance Variables:**

```java
public void anonymousInstanceVariables() {
    Runnable anonymous = new Runnable() {
        // ✅ Can have instance variables
        private String status = "ready";
        private int executionCount = 0;
        private List<String> logs = new ArrayList<>();
        
        @Override
        public void run() {
            executionCount++;
            logs.add("Execution #" + executionCount);
            status = "running";
            
            System.out.println("Status: " + status);
            System.out.println("Count: " + executionCount);
            System.out.println("Logs: " + logs);
            
            // Can even have methods that use these variables
            updateStatus();
        }
        
        private void updateStatus() {
            status = "completed";
        }
    };
}
```

### **Lambdas Cannot Have Instance Variables:**

```java
public void lambdaNoInstanceVariables() {
    Runnable lambda = () -> {
        // ❌ CANNOT have instance variables
        // private String status = "ready"; // COMPILATION ERROR!
        
        // ✅ Can have local variables
        String localStatus = "ready";
        int localCount = 1;
        
        System.out.println("Local status: " + localStatus);
        System.out.println("Local count: " + localCount);
        
        // ❌ CANNOT have methods
        // private void helper() { } // COMPILATION ERROR!
    };
}
```

## **Practical Examples and Use Cases**

### **When Anonymous Class Scope is Useful:**

```java
public class DatabaseConnection {
    private String connectionUrl = "jdbc:mysql://localhost";
    
    public void setupConnectionWithRetry() {
        TimerTask retryTask = new TimerTask() {
            private int attemptCount = 0;
            private boolean connected = false;
            private List<String> errorLog = new ArrayList<>();
            
            @Override
            public void run() {
                attemptCount++;
                try {
                    connect();
                    connected = true;
                    System.out.println("Connected after " + attemptCount + " attempts");
                } catch (Exception e) {
                    errorLog.add("Attempt " + attemptCount + ": " + e.getMessage());
                    if (attemptCount >= 5) {
                        System.out.println("Failed after 5 attempts: " + errorLog);
                        this.cancel(); // Cancel the timer task
                    }
                }
            }
            
            private void connect() throws Exception {
                // Connection logic using outer class field
                System.out.println("Attempting connection to " + connectionUrl);
                // Simulate connection...
            }
        };
        
        Timer timer = new Timer();
        timer.schedule(retryTask, 0, 5000); // Retry every 5 seconds
    }
}
```

### **When Lambda Scope is Sufficient:**

```java
public class EventProcessor {
    private List<String> processedEvents = new ArrayList<>();
    
    public void processEvents(List<String> events) {
        // Lambda scope is perfect for simple transformations
        events.stream()
              .filter(event -> event.startsWith("IMPORTANT"))
              .map(event -> event.toUpperCase())
              .forEach(event -> {
                  processedEvents.add(event); // Access outer class field
                  System.out.println("Processed: " + event);
              });
    }
}
```

## **Common Pitfalls and Solutions**

### **Pitfall 1: Variable Modification**

```java
public void variableModificationPitfall() {
    int counter = 0;
    List<String> items = Arrays.asList("A", "B", "C");
    
    // ❌ This won't work:
    // items.forEach(item -> counter++); // COMPILATION ERROR!
    
    // ✅ Solutions:
    
    // Solution 1: Use AtomicInteger
    AtomicInteger atomicCounter = new AtomicInteger(0);
    items.forEach(item -> atomicCounter.incrementAndGet());
    
    // Solution 2: Use array
    int[] counterArray = {0};
    items.forEach(item -> counterArray[0]++);
    
    // Solution 3: Use instance variable
    processItems(items);
}

private int instanceCounter = 0;
private void processItems(List<String> items) {
    items.forEach(item -> instanceCounter++); // ✅ Works with instance variables
}
```

### **Pitfall 2: `this` Reference Confusion**

```java
public class EventHandler {
    private String name = "MainHandler";
    
    public void confusingThisUsage() {
        // Anonymous class - different 'this'
        Runnable anonymous = new Runnable() {
            private String name = "AnonymousHandler";
            
            @Override
            public void run() {
                // Which name? The anonymous one!
                System.out.println(name); // "AnonymousHandler"
                System.out.println(this.name); // "AnonymousHandler"
                
                // To get outer name:
                System.out.println(EventHandler.this.name); // "MainHandler"
            }
        };
        
        // Lambda - same 'this'
        Runnable lambda = () -> {
            // Always refers to outer class
            System.out.println(name); // "MainHandler"
            System.out.println(this.name); // "MainHandler"
        };
    }
}
```

### **Pitfall 3: Trying to Modify Captured Variables**

```java
public void modificationAttempts() {
    String status = "initial";
    int count = 0;
    
    // ❌ These won't work:
    Runnable lambda = () -> {
        // status = "modified"; // COMPILATION ERROR!
        // count++; // COMPILATION ERROR!
    };
    
    // ✅ Correct approaches:
    
    // Use mutable objects
    StringBuilder statusBuilder = new StringBuilder("initial");
    AtomicInteger atomicCount = new AtomicInteger(0);
    
    Runnable workingLambda = () -> {
        statusBuilder.setLength(0);
        statusBuilder.append("modified"); // ✅ Works
        atomicCount.incrementAndGet(); // ✅ Works
    };
}
```

## **Summary of Variable Scope and Capture**

| Aspect                    | Anonymous Classes            | Lambda Expressions           |
|---------------------------|------------------------------|------------------------------|
| **Own Scope**             | ✅ Has separate scope         | ❌ Shares enclosing scope     |
| **Instance Variables**    | ✅ Can have private fields    | ❌ Cannot have fields         |
| **Instance Methods**      | ✅ Can have private methods   | ❌ Cannot have methods        |
| **`this` Reference**      | Points to anonymous instance | Points to enclosing instance |
| **Variable Capture**      | Must be effectively final    | Must be effectively final    |
| **Access to Outer Class** | `OuterClass.this.field`      | `this.field` or just `field` |
| **Memory Usage**          | Higher (separate object)     | Lower (shares context)       |

**Key Takeaway**: Anonymous classes are like creating a **"mini class with its own room"**, while
lambdas are like **"borrowing space in the current room"**. Choose based on whether you need the
extra features of having your own scope! 🏠

#### 1.3.5 `this` Reference Behavior (Critical Difference!)

**Anonymous Classes**:

```java
public class ThisReferenceExample {
    private String name = "OuterClass";
    
    public void demonstrateThis() {
        Runnable anonymous = new Runnable() {
            private String name = "AnonymousClass";
            
            @Override
            public void run() {
                System.out.println(this.name);           // "AnonymousClass"
                System.out.println(ThisReferenceExample.this.name); // "OuterClass"
            }
        };
    }
}
```

**Lambda Expressions**:

```java
public class ThisReferenceExample {
    private String name = "OuterClass";
    
    public void demonstrateThis() {
        Runnable lambda = () -> {
            System.out.println(this.name);  // "OuterClass" - refers to enclosing instance
            // No separate 'this' for lambda
        };
    }
}
```

#### 1.3.6 When to Use Which?

**Use Anonymous Classes When**:

- ✅ Interface has **multiple abstract methods**
- ✅ Need to **extend an abstract class**
- ✅ Need **instance variables** in the implementation
- ✅ Need **complex initialization** logic
- ✅ **Legacy codebase** (pre-Java 8)
- ✅ Need different **`this` semantics**

**Use Lambda Expressions When**:

- ✅ Target is a **functional interface**
- ✅ Simple, **single-line** implementations
- ✅ **Performance** is important
- ✅ Want **cleaner, more readable** code
- ✅ **Functional programming** style
- ✅ Working with **Streams API**

#### 1.3.7 Migration Example

**Before (Anonymous Classes)**:

```java
public class BeforeExample {
    public void processData() {
        List<String> data = getData();
        
        // Filter with anonymous class
        data.stream().filter(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return s.length() > 5;
            }
        });
        
        // Sort with anonymous class
        Collections.sort(data, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.compareToIgnoreCase(b);
            }
        });
        
        // Process with anonymous class
        data.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println("Processing: " + s);
            }
        });
    }
}
```

**After (Lambda Expressions)**:

```java
public class AfterExample {
    public void processData() {
        List<String> data = getData();
        
        data.stream()
            .filter(s -> s.length() > 5)                    // Lambda
            .sorted(String::compareToIgnoreCase)            // Method reference
            .forEach(s -> System.out.println("Processing: " + s)); // Lambda
    }
}
```

#### 1.3.8 Common Pitfalls and Solutions

**Pitfall 1: Variable capture confusion**

```java
public void pitfallExample() {
    List<Runnable> tasks = new ArrayList<>();
    
    // ❌ WRONG - Variable must be effectively final
    for (int i = 0; i < 10; i++) {
        // tasks.add(() -> System.out.println(i)); // Compilation error
    }
    
    // ✅ CORRECT - Use effectively final variable
    for (int i = 0; i < 10; i++) {
        final int index = i;
        tasks.add(() -> System.out.println(index));
    }
}
```

**Pitfall 2: `this` reference confusion**

```java
public class ThisPitfall {
    private String name = "OuterClass";
    
    public void createTask() {
        // Lambda - 'this' refers to OuterClass
        Runnable lambda = () -> System.out.println(this.name); // "OuterClass"
        
        // Anonymous class - 'this' refers to anonymous class
        Runnable anonymous = new Runnable() {
            private String name = "Anonymous";
            
            @Override
            public void run() {
                System.out.println(this.name); // "Anonymous" - different 'this'!
            }
        };
    }
}
```

**Summary Comparison Table**:

| Aspect                    | Anonymous Classes               | Lambda Expressions            |
|---------------------------|---------------------------------|-------------------------------|
| **Syntax**                | Verbose (5+ lines)              | Concise (1 line)              |
| **Performance**           | Slower, more memory             | Faster, less memory           |
| **Scope**                 | Own scope                       | Enclosing scope               |
| **`this` reference**      | Anonymous instance              | Enclosing instance            |
| **Interface requirement** | Any interface/abstract class    | Functional interfaces only    |
| **Instance variables**    | ✅ Allowed                       | ❌ Not allowed                 |
| **Multiple methods**      | ✅ Supported                     | ❌ Not supported               |
| **Debugging**             | Easier in some cases            | Modern tools handle well      |
| **Readability**           | More verbose                    | Much cleaner                  |
| **Best for**              | Complex logic, multiple methods | Simple, functional operations |

### 1.4 Variable Capture and Scope

**Effectively Final Variables**:

```java
int multiplier = 10; // Effectively final
List<Integer> numbers = Arrays.asList(1, 2, 3);
numbers.forEach(n -> System.out.println(n * multiplier));

// This would cause compilation error:
// multiplier = 20; // Cannot modify captured variable
```

**Rules**:

- Local variables must be effectively final
- Instance variables can be modified
- Static variables can be modified

### 1.5 Lambda Expressions with Collections

**Common Usage Patterns**:

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// forEach
names.forEach(name -> System.out.println(name));

// Sorting
names.sort((a, b) -> a.compareTo(b));

// Filtering (with streams)
names.stream()
     .filter(name -> name.startsWith("A"))
     .forEach(System.out::println);
```

---

## 2. Functional Interfaces

### 2.1 What are Functional Interfaces?

**Definition**: A functional interface is an interface that contains exactly one abstract method.
They can have multiple default and static methods but only one abstract method.

**@FunctionalInterface Annotation**:

```java
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
    
    // Default methods allowed
    default void printResult(int result) {
        System.out.println("Result: " + result);
    }
    
    // Static methods allowed
    static void info() {
        System.out.println("Calculator interface");
    }
}
```

### 2.2 Built-in Functional Interfaces

**java.util.function Package**:

#### 2.2.1 Predicate&lt;T&gt;

```java
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}

// Usage
Predicate<String> isEmpty = String::isEmpty;
Predicate<Integer> isPositive = x -> x > 0;

// Combining predicates
Predicate<String> isNotEmpty = isEmpty.negate();
Predicate<String> isLongAndNotEmpty = 
    ((Predicate<String>) s -> s.length() > 5).and(isEmpty.negate());
```

#### 2.2.2 Function&lt;T, R&gt;

```java
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}

// Usage
Function<String, Integer> stringLength = String::length;
Function<Integer, String> intToString = Object::toString;

// Function composition
Function<String, String> addExclamation = s -> s + "!";
Function<String, String> toUpperAndExclaim = 
    addExclamation.compose(String::toUpperCase);
```

#### 2.2.3 Consumer&lt;T&gt;

```java
@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}

// Usage
Consumer<String> printer = System.out::println;
Consumer<List<String>> listPrinter = list -> list.forEach(System.out::println);

// Chaining consumers
Consumer<String> printAndLog = printer.andThen(s -> logger.log(s));
```

#### 2.2.4 Supplier&lt;T&gt;

```java
@FunctionalInterface
public interface Supplier<T> {
    T get();
}

// Usage
Supplier<String> stringSupplier = () -> "Hello World";
Supplier<Double> randomSupplier = Math::random;
Supplier<List<String>> listSupplier = ArrayList::new;
```

#### 2.2.5 BiFunction&lt;T, U, R&gt;

```java
@FunctionalInterface
public interface BiFunction<T, U, R> {
    R apply(T t, U u);
}

// Usage
BiFunction<Integer, Integer, Integer> adder = (a, b) -> a + b;
BiFunction<String, String, String> concatenator = (a, b) -> a + b;
```

### 2.3 Specialized Functional Interfaces

**Primitive Specializations** (to avoid autoboxing):

- `IntPredicate`, `LongPredicate`, `DoublePredicate`
- `IntFunction<R>`, `IntToDoubleFunction`, `IntToLongFunction`
- `IntConsumer`, `LongConsumer`, `DoubleConsumer`
- `IntSupplier`, `LongSupplier`, `DoubleSupplier`, `BooleanSupplier`

```java
IntPredicate isEven = x -> x % 2 == 0;
IntConsumer printer = System.out::println;
IntSupplier randomInt = () -> (int)(Math.random() * 100);
```

---

## 3. Method References

### 3.1 Introduction to Method References

**Definition**: Method references provide a way to refer to methods without executing them. They're
shortcuts for lambda expressions that only call an existing method.

**Syntax**: `ClassName::methodName` or `instance::methodName`

        ### 3.2 Types of Method References

#### 3.2.1 Static Method References

```java
// Lambda
Function<String, Integer> lambda = s -> Integer.parseInt(s);

// Method reference
Function<String, Integer> methodRef = Integer::parseInt;

// Usage
List<String> numbers = Arrays.asList("1", "2", "3");
numbers.stream()
       .map(Integer::parseInt)
       .forEach(System.out::println);
```

#### 3.2.2 Instance Method References on Particular Object

```java
String prefix = "Hello ";
Function<String, String> lambda = s -> prefix.concat(s);
Function<String, String> methodRef = prefix::concat;

// With collections
List<String> names = Arrays.asList("Alice", "Bob");
names.forEach(System.out::println); // System.out::println
```

#### 3.2.3 Instance Method References on Arbitrary Object

```java
// Lambda
Function<String, String> lambda = s -> s.toUpperCase();

// Method reference
Function<String, String> methodRef = String::toUpperCase;

// With comparisons
List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
names.sort(String::compareToIgnoreCase);
```

#### 3.2.4 Constructor References

```java
// Lambda
Supplier<List<String>> lambda = () -> new ArrayList<>();

// Constructor reference
Supplier<List<String>> constructorRef = ArrayList::new;

// With parameters
Function<Integer, List<String>> listCreator = ArrayList::new;

// Array constructor references
Function<Integer, String[]> arrayCreator = String[]::new;
```

### 3.3 When to Use Method References

**Use Method References When**:

- The lambda expression only calls an existing method
- The method signature matches the functional interface
- It improves readability

**Example Conversions**:

```java
// Convert these lambdas to method references:
x -> System.out.println(x)     // System.out::println
x -> x.toString()              // Object::toString
(x, y) -> x.compareTo(y)       // String::compareTo (if x is String)
() -> new HashMap<>()          // HashMap::new
x -> Math.abs(x)               // Math::abs
```

---

## 4. Stream API

### 4.1 Introduction to Streams

**Definition**: A Stream is a sequence of elements supporting sequential and parallel aggregate
operations. Streams are not data structures; they're views of data sources.

**Key Characteristics**:

- **No storage**: Streams don't store elements
- **Functional**: Operations produce results without modifying source
- **Lazy**: Intermediate operations are lazy
- **Consumable**: Can be traversed only once

### 4.2 Stream Creation

#### 4.2.1 From Collections

```java
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream = list.stream();
Stream<String> parallelStream = list.parallelStream();
```

#### 4.2.2 From Arrays

```java
String[] array = {"a", "b", "c"};
Stream<String> stream = Arrays.stream(array);
Stream<String> partialStream = Arrays.stream(array, 1, 3); // b, c
```

#### 4.2.3 Static Factory Methods

```java
// Empty stream
Stream<String> empty = Stream.empty();

// Single element
Stream<String> single = Stream.of("a");

// Multiple elements
Stream<String> multiple = Stream.of("a", "b", "c");

// Infinite streams
Stream<Integer> infinite = Stream.iterate(0, n -> n + 2); // 0, 2, 4, 6...
Stream<Double> random = Stream.generate(Math::random);

// Finite range
IntStream range = IntStream.range(1, 5);        // 1, 2, 3, 4
IntStream rangeClosed = IntStream.rangeClosed(1, 5); // 1, 2, 3, 4, 5
```

#### 4.2.4 From Files and Other Sources

```java
// From file lines
Stream<String> lines = Files.lines(Paths.get("file.txt"));

// From regex pattern
Stream<String> words = Pattern.compile("\\W+")
                             .splitAsStream("Hello World Java");

// From BufferedReader
BufferedReader reader = Files.newBufferedReader(path);
Stream<String> lines = reader.lines();
```

### 4.3 Stream Operations

#### 4.3.1 Intermediate Operations (Lazy)

**filter(Predicate&lt;T&gt;)**:

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
names.stream()
     .filter(name -> name.length() > 4)
     .forEach(System.out::println); // Alice, Charlie
```

**map(Function&lt;T, R&gt;)**:

```java
names.stream()
     .map(String::toUpperCase)
     .forEach(System.out::println); // ALICE, BOB, CHARLIE, DAVID
```

**flatMap(Function&lt;T, Stream&lt;R&gt;&gt;)**:

```java
List<List<String>> nested = Arrays.asList(
    Arrays.asList("a", "b"),
    Arrays.asList("c", "d", "e")
);

nested.stream()
      .flatMap(List::stream)
      .forEach(System.out::println); // a, b, c, d, e
```

**distinct()**:

```java
Stream.of("a", "b", "a", "c", "b")
      .distinct()
      .forEach(System.out::println); // a, b, c
```

**sorted()**:

```java
Stream.of("Charlie", "Alice", "Bob")
      .sorted()
      .forEach(System.out::println); // Alice, Bob, Charlie

// With custom comparator
Stream.of("Charlie", "Alice", "Bob")
      .sorted(Comparator.comparing(String::length))
      .forEach(System.out::println); // Bob, Alice, Charlie
```

**limit(long n)** and **skip(long n)**:

```java
Stream.iterate(1, x -> x + 1)
      .limit(10)
      .skip(5)
      .forEach(System.out::println); // 6, 7, 8, 9, 10
```

**peek(Consumer&lt;T&gt;)** (for debugging):

```java
names.stream()
     .filter(name -> name.length() > 3)
     .peek(name -> System.out.println("Filtered: " + name))
     .map(String::toUpperCase)
     .peek(name -> System.out.println("Mapped: " + name))
     .forEach(System.out::println);
```

#### 4.3.2 Terminal Operations (Eager)

**forEach(Consumer&lt;T&gt;)** and **forEachOrdered(Consumer&lt;T&gt;)**:

```java
names.stream().forEach(System.out::println);
names.parallelStream().forEachOrdered(System.out::println); // Maintains order
```

**collect(Collector&lt;T, A, R&gt;)**:

```java
// To List
List<String> upperNames = names.stream()
                               .map(String::toUpperCase)
                               .collect(Collectors.toList());

// To Set
Set<String> nameSet = names.stream()
                           .collect(Collectors.toSet());

// To Map
Map<String, Integer> nameLengths = names.stream()
                                       .collect(Collectors.toMap(
                                           Function.identity(),
                                           String::length
                                       ));

// Grouping
Map<Integer, List<String>> groupedByLength = names.stream()
                                                 .collect(Collectors.groupingBy(String::length));

// Joining
String joined = names.stream()
                     .collect(Collectors.joining(", ", "[", "]"));
```

**reduce() Operations**:

```java
// Simple reduction
Optional<String> longest = names.stream()
                                .reduce((s1, s2) -> s1.length() > s2.length() ? s1 : s2);

// With identity
String concatenated = names.stream()
                           .reduce("", (s1, s2) -> s1 + s2);

// With combiner (for parallel processing)
String result = names.parallelStream()
                     .reduce("", 
                             (partial, element) -> partial + element,
                             (partial1, partial2) -> partial1 + partial2);
```

**Finding Operations**:

```java
Optional<String> first = names.stream()
                              .filter(name -> name.startsWith("A"))
                              .findFirst();

Optional<String> any = names.parallelStream()
                           .filter(name -> name.startsWith("A"))
                           .findAny();
```

**Matching Operations**:

```java
boolean allLong = names.stream().allMatch(name -> name.length() > 2);
boolean anyLong = names.stream().anyMatch(name -> name.length() > 5);
boolean noneLong = names.stream().noneMatch(name -> name.length() > 10);
```

**Counting and Statistics**:

```java
long count = names.stream().count();

// For numeric streams
IntSummaryStatistics stats = names.stream()
                                  .mapToInt(String::length)
                                  .summaryStatistics();
System.out.println(stats.getMax());
System.out.println(stats.getAverage());
```

#### 4.3.3 Primitive Streams

**IntStream, LongStream, DoubleStream**:

```java
// Creating primitive streams
IntStream intStream = IntStream.range(1, 10);
LongStream longStream = LongStream.rangeClosed(1, 1000);
DoubleStream doubleStream = DoubleStream.of(1.0, 2.0, 3.0);

// Converting to primitive streams
IntStream lengths = names.stream().mapToInt(String::length);
DoubleStream prices = products.stream().mapToDouble(Product::getPrice);

// Primitive stream operations
int sum = IntStream.range(1, 101).sum();
OptionalDouble average = IntStream.range(1, 101).average();
IntSummaryStatistics stats = IntStream.range(1, 101).summaryStatistics();

// Boxing back to object streams
Stream<Integer> boxed = IntStream.range(1, 10).boxed();
```

### 4.4 Advanced Stream Operations

#### 4.4.1 Custom Collectors

```java
// Custom collector example
Collector<String, ?, String> customJoiner = 
    Collector.of(
        StringBuilder::new,                    // supplier
        (sb, s) -> sb.append(s).append(" "),  // accumulator
        (sb1, sb2) -> sb1.append(sb2),        // combiner
        StringBuilder::toString               // finisher
    );

String result = names.stream().collect(customJoiner);
```

#### 4.4.2 Partitioning and Grouping

```java
// Partitioning (binary classification)
Map<Boolean, List<String>> partitioned = names.stream()
    .collect(Collectors.partitioningBy(name -> name.length() > 4));

// Multi-level grouping
Map<Integer, Map<Character, List<String>>> grouped = names.stream()
    .collect(Collectors.groupingBy(
        String::length,
        Collectors.groupingBy(name -> name.charAt(0))
    ));

// Downstream collectors
Map<Integer, Long> lengthCounts = names.stream()
    .collect(Collectors.groupingBy(
        String::length,
        Collectors.counting()
    ));
```

---

## 5. Parallel Streams

### 5.1 Introduction to Parallel Streams

**Definition**: Parallel streams divide the stream into multiple substreams, process them in
parallel using the Fork/Join framework, and combine the results.

**When to Use Parallel Streams**:

- Large datasets (typically > 10,000 elements)
- CPU-intensive operations
- Sufficient CPU cores available
- Operations are stateless and associative

### 5.2 Creating Parallel Streams

```java
// From collections
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Stream<Integer> parallelStream = numbers.parallelStream();

// Converting sequential to parallel
Stream<Integer> parallel = numbers.stream().parallel();

// Converting parallel to sequential
Stream<Integer> sequential = parallelStream.sequential();

// Check if stream is parallel
boolean isParallel = parallelStream.isParallel();
```

### 5.3 Parallel Stream Processing

#### 5.3.1 Basic Parallel Operations

```java
List<Integer> numbers = IntStream.range(1, 1000000)
                                 .boxed()
                                 .collect(Collectors.toList());

// Sequential processing
long sequentialSum = numbers.stream()
                           .mapToLong(Integer::longValue)
                           .sum();

// Parallel processing
long parallelSum = numbers.parallelStream()
                         .mapToLong(Integer::longValue)
                         .sum();
```

#### 5.3.2 Performance Considerations

```java
// CPU-intensive task that benefits from parallelization
List<Integer> numbers = IntStream.range(1, 100000)
                                 .boxed()
                                 .collect(Collectors.toList());

// Sequential - will be slower for CPU-intensive tasks
double sequentialResult = numbers.stream()
                                 .mapToDouble(ParallelStreamExample::expensiveOperation)
                                 .sum();

// Parallel - will be faster for CPU-intensive tasks
double parallelResult = numbers.parallelStream()
                              .mapToDouble(ParallelStreamExample::expensiveOperation)
                              .sum();

private static double expensiveOperation(int n) {
    // Simulate expensive computation
    return Math.sqrt(Math.sin(n) * Math.cos(n));
}
```

### 5.4 Fork/Join Framework

**How It Works**:

1. Stream is split into substreams
2. Each substream is processed on different threads
3. Results are combined using the combiner function

**Thread Pool**: Uses `ForkJoinPool.commonPool()` by default

```java
// Custom thread pool
ForkJoinPool customThreadPool = new ForkJoinPool(4);
try {
    long result = customThreadPool.submit(() ->
        numbers.parallelStream()
               .mapToLong(Integer::longValue)
               .sum()
    ).get();
} finally {
    customThreadPool.shutdown();
}
```

### 5.5 Parallel Stream Pitfalls

#### 5.5.1 Shared Mutable State

```java
// WRONG - Race condition
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> results = new ArrayList<>(); // Shared mutable state

numbers.parallelStream()
       .forEach(results::add); // Race condition!

// CORRECT - Use collectors
List<Integer> results = numbers.parallelStream()
                              .collect(Collectors.toList());
```

#### 5.5.2 Stateful Operations

```java
// WRONG - stateful operation
Stream.of("a", "b", "c", "d")
      .parallel()
      .map(s -> {
          // Stateful operation - unpredictable results
          return s + counter++;
      });

// CORRECT - stateless operation
AtomicInteger safeCounter = new AtomicInteger();
Stream.of("a", "b", "c", "d")
      .parallel()
      .map(s -> s + safeCounter.incrementAndGet());
```

#### 5.5.3 Order Sensitivity

```java
// For order-sensitive operations, use forEachOrdered
numbers.parallelStream()
       .filter(n -> n % 2 == 0)
       .forEachOrdered(System.out::println); // Maintains order
```

### 5.6 Performance Guidelines

**Use Parallel Streams When**:

- Dataset is large (> 10,000 elements)
- Operations are CPU-intensive
- Operations are stateless and side-effect free
- You have multiple CPU cores

**Avoid Parallel Streams When**:

- Dataset is small (< 1,000 elements)
- Operations are I/O bound
- Operations maintain state or have side effects
- Sequential order is critical and you can't use `forEachOrdered`

**Benchmarking Example**:

```java
public class ParallelStreamBenchmark {
    private static final int SIZE = 1_000_000;
    private List<Integer> numbers;
    
    @Setup
    public void setup() {
        numbers = IntStream.range(1, SIZE)
                          .boxed()
                          .collect(Collectors.toList());
    }
    
    public long sequentialSum() {
        return numbers.stream()
                     .mapToLong(Integer::longValue)
                     .sum();
    }
    
    public long parallelSum() {
        return numbers.parallelStream()
                     .mapToLong(Integer::longValue)
                     .sum();
    }
}
```

---

## 6. Optional

### 6.1 Introduction to Optional

**Definition**: `Optional<T>` is a container object that may or may not contain a non-null value.
It's designed to handle the absence of values gracefully and avoid `NullPointerException`.

**Purpose**:

- Explicit handling of "no value" scenarios
- Functional approach to null handling
- Method chaining for null-safe operations
- Clear API contracts (methods returning Optional signal potential absence)

### 6.2 Creating Optional Objects

#### 6.2.1 Factory Methods

```java
// Empty Optional
Optional<String> empty = Optional.empty();

// Optional with non-null value
Optional<String> present = Optional.of("Hello");

// Optional that might be null
String value = null;
Optional<String> maybe = Optional.ofNullable(value); // Returns empty Optional

// NEVER do this - will throw NullPointerException
Optional<String> wrong = Optional.of(null); // Throws NPE!
```

#### 6.2.2 From Method Returns

```java
public Optional<User> findUserById(Long id) {
    User user = database.findUser(id); // might return null
    return Optional.ofNullable(user);
}

public Optional<String> getFirstElement(List<String> list) {
    return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
}
```

### 6.3 Checking for Values

#### 6.3.1 Basic Presence Checks

```java
Optional<String> optional = Optional.of("Hello");

// Check if value is present
if (optional.isPresent()) {
    System.out.println(optional.get()); // Hello
}

// Check if value is empty (Java 11+)
if (optional.isEmpty()) {
    System.out.println("No value");
}
```

#### 6.3.2 Functional Approaches

```java
Optional<String> optional = Optional.of("Hello");

// Execute action if present
optional.ifPresent(System.out::println); // Hello

// Execute action if present, else execute another action (Java 9+)
optional.ifPresentOrElse(
    value -> System.out.println("Found: " + value),
    () -> System.out.println("Not found")
);
```

### 6.4 Retrieving Values

#### 6.4.1 Basic Retrieval

```java
Optional<String> optional = Optional.of("Hello");

// Get value (throws NoSuchElementException if empty)
String value = optional.get(); // Avoid this - use alternatives below

// Get value or return default
String value = optional.orElse("Default");

// Get value or compute default lazily
String value = optional.orElseGet(() -> computeDefault());

// Get value or throw custom exception
String value = optional.orElseThrow(() -> new CustomException("Value not found"));

// Get value or throw (Java 10+)
String value = optional.orElseThrow(); // Throws NoSuchElementException
```

#### 6.4.2 Default Value Strategies

```java
// Eager evaluation (always computes default)
String result = optional.orElse(expensiveDefault());

// Lazy evaluation (computes default only if needed)
String result = optional.orElseGet(() -> expensiveDefault());

// Custom exception
String result = optional.orElseThrow(() -> new IllegalStateException("Required value missing"));
```

### 6.5 Transforming Optional Values

#### 6.5.1 map() Operation

```java
Optional<String> name = Optional.of("John");

// Transform value if present
Optional<Integer> nameLength = name.map(String::length); // Optional[4]
Optional<String> upperName = name.map(String::toUpperCase); // Optional[JOHN]

// Chain transformations
Optional<String> result = name
    .map(String::toUpperCase)
    .map(s -> s + "!")
    .map(s -> "Hello, " + s); // Optional[Hello, JOHN!]

// If optional is empty, map returns empty
Optional<String> empty = Optional.empty();
Optional<Integer> length = empty.map(String::length); // Optional.empty()
```

#### 6.5.2 flatMap() Operation

```java
public Optional<Address> getAddress(User user) {
    return Optional.ofNullable(user.getAddress());
}

public Optional<String> getCity(User user) {
    return Optional.ofNullable(user)
                   .flatMap(this::getAddress)
                   .map(Address::getCity);
}

// Avoid nested Optionals
Optional<Optional<String>> nested = user.map(u -> getAddress(u).map(Address::getCity));
Optional<String> flattened = user.flatMap(u -> getAddress(u).map(Address::getCity));
```

### 6.6 Filtering Optional Values

```java
Optional<String> name = Optional.of("John");

// Filter based on predicate
Optional<String> longName = name.filter(n -> n.length() > 5); // Optional.empty()
Optional<String> shortName = name.filter(n -> n.length() <= 5); // Optional[John]

// Combining filter and map
Optional<String> result = name
    .filter(n -> n.startsWith("J"))
    .map(String::toUpperCase)
    .filter(n -> n.length() > 3); // Optional[JOHN]
```

### 6.7 Optional with Streams

#### 6.7.1 Stream of Optionals

```java
List<Optional<String>> optionals = Arrays.asList(
    Optional.of("A"),
    Optional.empty(),
    Optional.of("B"),
    Optional.empty(),
    Optional.of("C")
);

// Extract present values (Java 9+)
List<String> values = optionals.stream()
                               .flatMap(Optional::stream)
                               .collect(Collectors.toList()); // [A, B, C]

// Pre-Java 9 approach
List<String> values = optionals.stream()
                               .filter(Optional::isPresent)
                               .map(Optional::get)
                               .collect(Collectors.toList());
```

#### 6.7.2 Optional in Stream Processing

```java
List<User> users = getUsers();

// Find first admin user
Optional<User> firstAdmin = users.stream()
                                 .filter(User::isAdmin)
                                 .findFirst();

// Get email of first admin or default
String adminEmail = users.stream()
                         .filter(User::isAdmin)
                         .findFirst()
                         .map(User::getEmail)
                         .orElse("admin@company.com");
```

### 6.8 Optional Best Practices

#### 6.8.1 Do's

```java
// DO: Use Optional as return type for methods that might not have a result
public Optional<User> findUserByEmail(String email) {
    return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
}

// DO: Use orElse for simple defaults
String name = optionalName.orElse("Unknown");

// DO: Use orElseGet for expensive computations
String name = optionalName.orElseGet(() -> generateDefaultName());

// DO: Use ifPresent for side effects
optional.ifPresent(value -> logger.log("Found: " + value));

// DO: Chain operations functionally
String result = optionalString
    .filter(s -> s.length() > 0)
    .map(String::toUpperCase)
    .orElse("EMPTY");
```

#### 6.8.2 Don'ts

```java
// DON'T: Use Optional for fields
public class User {
    private Optional<String> name; // BAD - use null instead
}

// DON'T: Use Optional for parameters
public void setName(Optional<String> name) { // BAD - use overloading or null
}

// DON'T: Use isPresent() + get()
if (optional.isPresent()) {
    String value = optional.get(); // BAD - use ifPresent or orElse
}

// DON'T: Create unnecessary Optional objects
Optional<String> optional = Optional.ofNullable(getString()); // BAD if getString never returns null

// DON'T: Use Optional.of with potentially null values
Optional<String> optional = Optional.of(potentiallyNull); // BAD - use ofNullable
```

### 6.9 Optional Anti-patterns

#### 6.9.1 Common Mistakes

```java
// Anti-pattern 1: Optional as field
class Person {
    private Optional<String> middleName; // Wrong!
    // Should be: private String middleName; (can be null)
}

// Anti-pattern 2: Optional parameters
public void updateUser(Long id, Optional<String> name) { // Wrong!
    // Should use method overloading or null checks
}

// Anti-pattern 3: Returning null from Optional method
public Optional<String> getName() {
    return null; // Wrong! Should return Optional.empty()
}

// Anti-pattern 4: isPresent() + get() instead of functional style
Optional<String> opt = getOptional();
if (opt.isPresent()) {
    System.out.println(opt.get()); // Wrong!
}
// Should be: opt.ifPresent(System.out::println);
```

### 6.10 Advanced Optional Patterns

#### 6.10.1 Chaining Optionals

```java
public Optional<String> getCountryCode(Long userId) {
    return findUser(userId)
        .flatMap(User::getAddress)
        .flatMap(Address::getCountry)
        .map(Country::getCode);
}
```

#### 6.10.2 Optional with Exception Handling

```java
public Optional<Integer> safeParseInt(String str) {
    try {
        return Optional.of(Integer.parseInt(str));
    } catch (NumberFormatException e) {
        return Optional.empty();
    }
}

// Or using a utility method
public static <T> Optional<T> tryOptional(Supplier<T> supplier) {
    try {
        return Optional.of(supplier.get());
    } catch (Exception e) {
        return Optional.empty();
    }
}

Optional<Integer> result = tryOptional(() -> Integer.parseInt("123"));
```

---

## 7. Best Practices and Performance

### 7.1 Lambda Expression Best Practices

#### 7.1.1 Keep Lambdas Simple and Readable

```java
// Good - simple and clear
list.stream().filter(s -> s.length() > 5).collect(toList());

// Bad - complex lambda
list.stream().filter(s -> {
    if (s == null) return false;
    String trimmed = s.trim();
    return trimmed.length() > 5 && trimmed.startsWith("A");
}).collect(toList());

// Better - extract to method
list.stream().filter(this::isValidString).collect(toList());

private boolean isValidString(String s) {
    if (s == null) return false;
    String trimmed = s.trim();
    return trimmed.length() > 5 && trimmed.startsWith("A");
}
```

#### 7.1.2 Prefer Method References When Appropriate

```java
// Good
list.stream().map(String::toUpperCase).collect(toList());

// Less readable
list.stream().map(s -> s.toUpperCase()).collect(toList());
```

#### 7.1.3 Avoid Side Effects in Lambdas

```java
// Bad - side effect
List<String> results = new ArrayList<>();
stream.filter(s -> s.length() > 3)
      .forEach(results::add); // Side effect!

// Good - functional approach
List<String> results = stream
    .filter(s -> s.length() > 3)
    .collect(Collectors.toList());
```

### 7.2 Stream API Best Practices

#### 7.2.1 Choose Appropriate Operations

```java
// Prefer anyMatch over filter + findFirst
// Bad
boolean hasLongName = names.stream()
                          .filter(name -> name.length() > 10)
                          .findFirst()
                          .isPresent();

// Good
boolean hasLongName = names.stream()
                          .anyMatch(name -> name.length() > 10);
```

#### 7.2.2 Use Primitive Streams for Performance

```java
// Bad - boxing/unboxing overhead
int sum = numbers.stream()
                 .mapToInt(Integer::intValue)
                 .sum();

// Good - use primitive stream directly
int sum = numbers.stream()
                 .mapToInt(i -> i)
                 .sum();

// Better - if numbers is already int[]
int sum = Arrays.stream(intArray).sum();
```

#### 7.2.3 Collect vs Reduce

```java
// Use collect for mutable containers
List<String> result = stream.collect(Collectors.toList());

// Use reduce for immutable results
Optional<String> result = stream.reduce((a, b) -> a + b);
```

### 7.3 Performance Considerations

#### 7.3.1 Stream vs Traditional Loops

```java
// For simple operations, traditional loops might be faster
List<Integer> numbers = getNumbers();

// Traditional loop - often faster for simple operations
int sum = 0;
for (Integer number : numbers) {
    sum += number;
}

// Stream - more readable but might have overhead
int sum = numbers.stream().mapToInt(Integer::intValue).sum();
```

#### 7.3.2 Lazy Evaluation Benefits

```java
// Only processes elements until first match is found
Optional<String> result = veryLargeList.stream()
    .filter(s -> expensiveCheck(s))
    .findFirst(); // Stops at first match
```

#### 7.3.3 Parallel Stream Guidelines

```java
// Good candidate for parallel processing
long result = IntStream.range(1, 1_000_000)
                      .parallel()
                      .filter(n -> isPrime(n))
                      .count();

// Bad candidate - too much overhead for simple operation
List<String> upperCase = shortList.parallelStream()
                                  .map(String::toUpperCase)
                                  .collect(toList());
```

### 7.4 Memory and Resource Management

#### 7.4.1 Close Resources Properly

```java
// Use try-with-resources for file streams
try (Stream<String> lines = Files.lines(path)) {
    return lines.filter(line -> line.contains("error"))
                .collect(Collectors.toList());
}
```

#### 7.4.2 Avoid Infinite Streams Without Limits

```java
// Bad - infinite stream without limit
Stream.iterate(1, n -> n + 1)
      .forEach(System.out::println); // Will run forever!

// Good - limit infinite streams
Stream.iterate(1, n -> n + 1)
      .limit(100)
      .forEach(System.out::println);
```

### 7.5 Common Anti-patterns and Solutions

#### 7.5.1 Collecting to Process Again

```java
// Anti-pattern
List<String> filtered = list.stream()
                           .filter(predicate)
                           .collect(toList());
filtered.forEach(System.out::println);

// Better
list.stream()
    .filter(predicate)
    .forEach(System.out::println);
```

#### 7.5.2 Overusing Streams

```java
// Unnecessary stream for simple operations
// Bad
String first = list.stream().findFirst().orElse(null);

// Good
String first = list.isEmpty() ? null : list.get(0);
```

### 7.6 Testing Strategies

#### 7.6.1 Testing Lambda-Heavy Code

```java
public class StreamProcessor {
    private final Predicate<String> validator;
    
    public StreamProcessor(Predicate<String> validator) {
        this.validator = validator;
    }
    
    public List<String> processStrings(List<String> input) {
        return input.stream()
                   .filter(validator)
                   .map(String::toUpperCase)
                   .collect(toList());
    }
}

// Test
@Test
public void testProcessStrings() {
    Predicate<String> lengthValidator = s -> s.length() > 3;
    StreamProcessor processor = new StreamProcessor(lengthValidator);
    
    List<String> input = Arrays.asList("a", "hello", "world");
    List<String> result = processor.processStrings(input);
    
    assertEquals(Arrays.asList("HELLO", "WORLD"), result);
}
```

---

## Summary

This comprehensive reviewer covers all essential aspects of Java Streams and Lambdas:

1. **Lambda Expressions**: Anonymous functions that enable functional programming
2. **Functional Interfaces**: Single abstract method interfaces that lambdas implement
3. **Method References**: Shortcuts for lambdas that only call existing methods
4. **Stream API**: Functional approach to processing collections with lazy evaluation
5. **Parallel Streams**: Leveraging multiple cores for stream processing
6. **Optional**: Container for handling potential absence of values gracefully

**Key Takeaways**:

- Lambdas make code more concise and functional
- Streams provide powerful, declarative data processing
- Parallel streams can improve performance for large datasets and CPU-intensive operations
- Optional helps eliminate NullPointerExceptions and makes null-handling explicit
- Performance considerations are important - not everything should be streams
- Functional programming principles apply: prefer immutable operations, avoid side effects

**Recommended Books for Further Study**:

- *Java: The Complete Reference* by Herbert Schildt
- *Effective Java* by Joshua Bloch
- *Java 8 in Action* by Raoul-Gabriel Urma, Mario Fusco, Alan Mycroft
- *Modern Java in Action* by Raoul-Gabriel Urma, Mario Fusco, Alan Mycroft
- *Java SE 8 for the Really Impatient* by Cay Horstmann

---

---

## Comprehensive Interview Cheat Sheet 🚀

### Quick Definitions (30-Second Elevator Pitch)

**Lambda Expression**

```java
(params) -> expression  // Anonymous function for functional interfaces
// Example: list.forEach(item -> System.out.println(item));
```

- **What**: Anonymous function implementing functional interface
- **Why**: Concise code, better performance than anonymous classes
- **When**: Single abstract method interfaces, simple operations
- **Key Rule**: Only captures effectively final variables

**Stream API**

```java
collection.stream().filter().map().collect()  // Declarative data processing
```

- **What**: Sequence of elements supporting aggregate operations
- **Why**: Functional, lazy, immutable, declarative style
- **When**: Complex data transformations, filtering, reducing
- **Key Rule**: Consumed only once, lazy evaluation

**Optional**

```java
Optional.ofNullable(value).orElse("default")  // Null-safe container
```

- **What**: Container that may/may not contain value
- **Why**: Explicit null handling, prevents NPE
- **When**: Return types that might be null
- **Key Rule**: Never use for fields/parameters

**Functional Interfaces**

```java
@FunctionalInterface
interface MyFunction { void doSomething(); }
```

- **What**: Interface with exactly one abstract method
- **Why**: Enables lambda expressions
- **When**: Need simple contracts for lambdas
- **Key Rule**: Can have default/static methods

**Method References**

```java
list.sort(String::compareToIgnoreCase)  // Shortcut for lambdas
```

- **What**: Shorthand for lambdas calling existing methods
- **Why**: More readable than equivalent lambdas
- **When**: Lambda only calls existing method
- **Key Rule**: Method signature must match functional interface

**Parallel Streams**

```java
bigList.parallelStream().filter().collect()  // Multi-threaded processing
```

- **What**: Streams processed across multiple threads
- **Why**: Better performance for large datasets
- **When**: CPU-intensive ops, large data, multi-core systems
- **Key Rule**: Must be stateless and thread-safe

---

## Core Functional Interfaces - Complete Reference

### Primary Interfaces

| Interface           | Method       | Input→Output | Use Case             | Example                   |
|---------------------|--------------|--------------|----------------------|---------------------------|
| `Predicate<T>`      | `test(T)`    | T → boolean  | Filtering, testing   | `x -> x > 0`              |
| `Function<T,R>`     | `apply(T)`   | T → R        | Transforming         | `String::length`          |
| `Consumer<T>`       | `accept(T)`  | T → void     | Side effects         | `System.out::println`     |
| `Supplier<T>`       | `get()`      | () → T       | Generating           | `() -> new ArrayList<>()` |
| `UnaryOperator<T>`  | `apply(T)`   | T → T        | Same type transform  | `x -> x * 2`              |
| `BinaryOperator<T>` | `apply(T,T)` | (T,T) → T    | Combining same types | `(a,b) -> a + b`          |

### Bi-Parameter Interfaces

| Interface           | Method        | Input→Output    | Use Case             | Example                 |
|---------------------|---------------|-----------------|----------------------|-------------------------|
| `BiPredicate<T,U>`  | `test(T,U)`   | (T,U) → boolean | Two-arg testing      | `(x,y) -> x > y`        |
| `BiFunction<T,U,R>` | `apply(T,U)`  | (T,U) → R       | Two-arg transform    | `(x,y) -> x + y`        |
| `BiConsumer<T,U>`   | `accept(T,U)` | (T,U) → void    | Two-arg side effects | `(k,v) -> map.put(k,v)` |

### Primitive Specializations (Avoid Boxing!)

| Type    | Interface           | Method                | Example                  |
|---------|---------------------|-----------------------|--------------------------|
| **Int** | `IntPredicate`      | `test(int)`           | `x -> x % 2 == 0`        |
|         | `IntFunction<R>`    | `apply(int)`          | `x -> String.valueOf(x)` |
|         | `IntConsumer`       | `accept(int)`         | `System.out::println`    |
|         | `IntSupplier`       | `getAsInt()`          | `() -> random.nextInt()` |
|         | `IntUnaryOperator`  | `applyAsInt(int)`     | `x -> x * x`             |
|         | `IntBinaryOperator` | `applyAsInt(int,int)` | `(a,b) -> a + b`         |

---

## Stream Operations - Complete Breakdown

### Intermediate Operations (Lazy - Build Pipeline)

#### Filtering & Selecting

```java
// filter() - Keep elements matching predicate
stream.filter(x -> x > 10)                    // Numbers > 10
stream.filter(String::isEmpty)                // Empty strings
stream.filter(Objects::nonNull)               // Non-null elements

// distinct() - Remove duplicates
stream.distinct()                             // Uses equals()
stream.distinct().sorted()                    // Unique + sorted

// limit() & skip() - Take/drop elements
stream.limit(5)                               // First 5 elements
stream.skip(3)                                // Skip first 3
stream.skip(10).limit(5)                      // Elements 11-15
```

#### Transforming
```java
// map() - Transform each element 1:1
stream.map(String::toUpperCase)               // String transformation
stream.map(Person::getName)                   // Extract property
stream.mapToInt(String::length)               // To IntStream

// flatMap() - Flatten nested structures
listOfLists.stream().flatMap(List::stream)    // List<List<T>> → Stream<T>
words.stream().flatMap(w -> Arrays.stream(w.split(""))) // Split words to chars
people.stream().flatMap(p -> p.getAddresses().stream()) // Extract all addresses
```

#### Sorting & Ordering

```java
// sorted() - Natural ordering
stream.sorted()                               // Comparable elements

// sorted(Comparator) - Custom ordering
stream.sorted(String.CASE_INSENSITIVE_ORDER)
stream.sorted(Comparator.comparing(Person::getAge))
stream.sorted(Comparator.comparing(Person::getAge).reversed())
stream.sorted(Comparator.comparing(Person::getName)
             .thenComparing(Person::getAge))
```

#### Debugging & Monitoring
```java
// peek() - Perform action without consuming
stream.peek(System.out::println)              // Debug stream contents
stream.peek(x -> logger.debug("Processing: " + x))
stream.filter(x -> x > 0)
      .peek(x -> System.out.println("Positive: " + x))
      .map(x -> x * 2)
      .peek(x -> System.out.println("Doubled: " + x))
```

### Terminal Operations (Eager - Execute Pipeline)

#### Collection Operations

```java
// collect() - Most versatile
stream.collect(Collectors.toList())           // → List<T>
stream.collect(Collectors.toSet())            // → Set<T>
stream.collect(Collectors.toCollection(LinkedList::new)) // → Specific collection

// Joining strings
stream.collect(Collectors.joining())          // Concatenate
stream.collect(Collectors.joining(", "))      // With separator
stream.collect(Collectors.joining(", ", "[", "]")) // With prefix/suffix

// Grouping and partitioning
stream.collect(Collectors.groupingBy(Person::getDepartment)) // → Map<String, List<Person>>
stream.collect(Collectors.partitioningBy(x -> x > 0))       // → Map<Boolean, List<T>>
stream.collect(Collectors.counting())         // → Long (count)
```

#### Reduction Operations

```java
// reduce() - Combine elements
stream.reduce((a, b) -> a + b)                // Optional<T>
stream.reduce(0, (a, b) -> a + b)             // T (with identity)
stream.reduce(0, Integer::sum, Integer::sum)  // For parallel streams

// Specialized reductions
intStream.sum()                               // int
intStream.max()                               // OptionalInt
intStream.average()                           // OptionalDouble
intStream.summaryStatistics()                 // IntSummaryStatistics
```

#### Finding & Matching
```java
// Finding elements
stream.findFirst()                            // Optional<T>
stream.findAny()                              // Optional<T> (parallel-friendly)

// Testing predicates
stream.allMatch(x -> x > 0)                   // boolean
stream.anyMatch(x -> x > 100)                 // boolean
stream.noneMatch(x -> x < 0)                  // boolean

// Counting
stream.count()                                // long
```

#### Iteration

```java
// forEach() - Side effects
stream.forEach(System.out::println)          // Unordered (parallel)
stream.forEachOrdered(System.out::println)   // Ordered (even parallel)
```

---

## Method References - Complete Guide

### Four Types with Real Examples

#### 1. Static Method References (`Class::method`)

```java
// Lambda → Method Reference
Function<String, Integer> lambda = s -> Integer.parseInt(s);
Function<String, Integer> methodRef = Integer::parseInt;

// Real usage scenarios
List<String> numbers = Arrays.asList("1", "2", "3");
List<Integer> ints = numbers.stream()
                            .map(Integer::parseInt)     // Parse strings
                            .collect(toList());

Arrays.stream(values).map(Math::abs)                   // Absolute values
people.stream().sorted(Comparator.comparing(Person::getName))
```

#### 2. Instance Method on Particular Object (`object::method`)
```java
// Lambda → Method Reference
String prefix = "Hello ";
Function<String, String> lambda = s -> prefix.concat(s);
Function<String, String> methodRef = prefix::concat;

// Real usage scenarios
PrintWriter writer = new PrintWriter(file);
lines.stream().forEach(writer::println);              // Write each line

Logger logger = LoggerFactory.getLogger(MyClass.class);
errors.stream().forEach(logger::error);               // Log each error

List<String> results = new ArrayList<>();
stream.forEach(results::add);                         // Add to specific list
```

#### 3. Instance Method on Arbitrary Object (`Class::method`)

```java
// Lambda → Method Reference
Function<String, String> lambda = s -> s.toUpperCase();
Function<String, String> methodRef = String::toUpperCase;

// Real usage scenarios
List<String> words = Arrays.asList("hello", "world");
words.stream().map(String::toUpperCase)               // Transform each
             .sorted(String::compareToIgnoreCase)     // Sort ignoring case
             .forEach(System.out::println);

people.stream().map(Person::getName)                  // Extract names
              .filter(String::isEmpty)                // Filter empty
              .count();
```

#### 4. Constructor References (`Class::new`)
```java
// Lambda → Constructor Reference
Supplier<List<String>> lambda = () -> new ArrayList<>();
Supplier<List<String>> constructorRef = ArrayList::new;

// Real usage scenarios
Stream<Person> people = names.stream()
                             .map(Person::new);        // Create Person from name

// Array creation
Function<Integer, String[]> arrayCreator = String[]::new;
String[] array = stream.toArray(String[]::new);

// Collection creation
List<String> list = stream.collect(toCollection(ArrayList::new));
Set<String> set = stream.collect(toCollection(TreeSet::new));
```

---

## Lambda vs Anonymous Classes - Decision Matrix

### Use Lambda When:
```java
// ✅ Functional interface (single abstract method)
Runnable task = () -> System.out.println("Task");
Comparator<String> comp = String::compareToIgnoreCase;
Predicate<Integer> isEven = x -> x % 2 == 0;

// ✅ Simple operations
list.stream().filter(x -> x > 0).map(x -> x * 2);

// ✅ Performance matters (2-3x faster)
// ✅ Working with Streams API
// ✅ Functional programming style
```

### Use Anonymous Classes When:
```java
// ✅ Multiple abstract methods
interface MultiMethod {
    void method1();
    void method2();
}
MultiMethod impl = new MultiMethod() {
    public void method1() { /* impl */ }
    public void method2() { /* impl */ }
};

// ✅ Need instance variables
TimerTask task = new TimerTask() {
    private int attempts = 0;
    private List<String> errors = new ArrayList<>();
    
    public void run() {
        attempts++;
        // Use instance variables
    }
};

// ✅ Extend abstract classes
Thread worker = new Thread() {
    public void run() { /* work */ }
};

// ✅ Complex initialization logic
// ✅ Need different 'this' semantics
```

---

## Variable Scope & Capture Rules

### Effectively Final Rule
```java
public void scopeExample() {
    // ✅ Effectively final - can capture
    String message = "Hello";
    int count = 5;
    
    // ❌ NOT effectively final - cannot capture
    int counter = 0;
    counter++; // Modifies variable
    
    // Lambda usage
    stream.forEach(item -> {
        System.out.println(message);  // ✅ OK
        System.out.println(count);    // ✅ OK
        // System.out.println(counter); // ❌ Compilation error
    });
}
```

### Workarounds for Variable Modification
```java
public void workarounds() {
    List<String> items = Arrays.asList("A", "B", "C");
    
    // ❌ Can't modify primitive
    int counter = 0;
    // items.forEach(item -> counter++); // ERROR!
    
    // ✅ Solution 1: AtomicInteger
    AtomicInteger atomicCounter = new AtomicInteger(0);
    items.forEach(item -> atomicCounter.incrementAndGet());
    
    // ✅ Solution 2: Array wrapper
    int[] counterArray = {0};
    items.forEach(item -> counterArray[0]++);
    
    // ✅ Solution 3: Instance variable
    // (move to instance method)
    
    // ✅ Solution 4: Collect results
    long count = items.stream().count(); // Use stream operations instead
}
```

### This Reference Behavior
```java
public class ThisExample {
    private String name = "Outer";
    
    public void demonstrateThis() {
        // Anonymous class - separate 'this'
        Runnable anonymous = new Runnable() {
            private String name = "Anonymous";
            public void run() {
                System.out.println(this.name);           // "Anonymous"
                System.out.println(ThisExample.this.name); // "Outer"
            }
        };
        
        // Lambda - shares enclosing 'this'
        Runnable lambda = () -> {
            System.out.println(this.name);  // "Outer"
            System.out.println(name);       // "Outer"
        };
    }
}
```

---

## Parallel Streams - Complete Decision Guide

### When to Use Parallel Streams

#### ✅ Perfect Candidates

```java
// Large datasets (> 10,000 elements)
List<Integer> bigList = IntStream.range(1, 1_000_000).boxed().collect(toList());

// CPU-intensive operations
bigList.parallelStream()
       .filter(this::isPrime)           // CPU-intensive
       .mapToDouble(Math::sqrt)         // More CPU work
       .sum();

// Independent operations (stateless)
words.parallelStream()
     .map(String::toUpperCase)          // Each independent
     .filter(s -> s.length() > 5)      // Each independent
     .collect(toList());

// Associative operations
numbers.parallelStream()
       .reduce(0, Integer::sum);        // Addition is associative
```

#### ❌ Avoid Parallel For

```java
// Small datasets (< 1,000 elements)
shortList.stream()  // NOT .parallelStream()
         .forEach(System.out::println);

// I/O operations
files.stream()      // NOT .parallelStream()
     .map(this::readFile)               // I/O bound
     .collect(toList());

// Order-dependent operations
items.stream()      // NOT .parallelStream()
     .forEach(System.out::println);    // Order matters

// Stateful operations
Stream.of("a", "b", "c")
      .map(s -> s + counter++)          // Stateful - avoid parallel
```

### Parallel Stream Pitfalls & Solutions

#### Race Conditions

```java
// ❌ WRONG - Race condition
List<String> results = new ArrayList<>();  // Not thread-safe
stream.parallel().forEach(results::add);   // Race condition!

// ✅ CORRECT - Use collectors
List<String> results = stream.parallel().collect(toList());

// ✅ CORRECT - Thread-safe collection
List<String> results = Collections.synchronizedList(new ArrayList<>());
stream.parallel().forEach(results::add);

// ✅ CORRECT - Concurrent collection
ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
stream.parallel().forEach(results::add);
```

#### Maintaining Order

```java
// ✅ For order-sensitive operations
numbers.parallelStream()
       .filter(n -> n % 2 == 0)
       .forEachOrdered(System.out::println);  // Maintains order

// ✅ Or use sequential for order-critical parts
numbers.parallelStream()
       .filter(this::expensiveFilter)         // Parallel
       .sequential()                          // Switch to sequential
       .forEach(System.out::println);        // Ordered output
```

---

## Optional - Complete Usage Patterns

### Creation Patterns

```java
// Factory methods
Optional<String> empty = Optional.empty();                    // Empty
Optional<String> present = Optional.of("value");              // Non-null value
Optional<String> nullable = Optional.ofNullable(getValue());  // Might be null

// From method returns
public Optional<User> findUser(Long id) {
    User user = database.findUser(id);
    return Optional.ofNullable(user);           // Handle potential null
}
```

### Retrieval Patterns

```java
Optional<String> opt = getOptional();

// Basic retrieval
String value = opt.orElse("default");                         // Simple default
String value = opt.orElseGet(() -> computeExpensiveDefault()); // Lazy default
String value = opt.orElseThrow();                             // Throw if empty
String value = opt.orElseThrow(() -> new CustomException("Not found"));

// Conditional actions
opt.ifPresent(System.out::println);                           // If present
opt.ifPresentOrElse(                                          // If/else (Java 9+)
    value -> System.out.println("Found: " + value),
    () -> System.out.println("Not found")
);
```

### Transformation Patterns

```java
Optional<String> name = Optional.of("John");

// map() - Transform if present
Optional<Integer> length = name.map(String::length);          // Optional[4]
Optional<String> upper = name.map(String::toUpperCase);       // Optional[JOHN]

// Chaining transformations
Optional<String> result = name
    .map(String::toUpperCase)
    .map(s -> s + "!")
    .map(s -> "Hello, " + s);                                 // Optional[Hello, JOHN!]

// filter() - Keep if matches predicate
Optional<String> longName = name.filter(n -> n.length() > 5); // Optional.empty()

// flatMap() - Avoid nested Optionals
public Optional<String> getCountryCode(Long userId) {
    return findUser(userId)                    // Optional<User>
        .flatMap(User::getAddress)             // Optional<Address>
        .flatMap(Address::getCountry)          // Optional<Country>
        .map(Country::getCode);                // Optional<String>
}
```

### Stream Integration

```java
// Stream of Optionals → Stream of values (Java 9+)
List<Optional<String>> optionals = Arrays.asList(
    Optional.of("A"), Optional.empty(), Optional.of("B")
);

List<String> values = optionals.stream()
                               .flatMap(Optional::stream)    // Java 9+
                               .collect(toList());           // [A, B]

// Pre-Java 9 approach
List<String> values = optionals.stream()
                               .filter(Optional::isPresent)
                               .map(Optional::get)
                               .collect(toList());

// Optional in stream processing
String adminEmail = users.stream()
                         .filter(User::isAdmin)
                         .findFirst()                      // Optional<User>
                         .map(User::getEmail)              // Optional<String>
                         .orElse("admin@company.com");
```

### Anti-Patterns (Don't Do These!)

```java
// ❌ Optional as field
public class User {
    private Optional<String> name;  // WRONG! Use null instead
}

// ❌ Optional parameters
public void setName(Optional<String> name) {  // WRONG! Use overloading
}

// ❌ isPresent() + get()
if (opt.isPresent()) {
    String value = opt.get();  // WRONG! Use ifPresent() or orElse()
}

// ❌ Optional.of() with nullable value
Optional<String> opt = Optional.of(mightBeNull);  // WRONG! Use ofNullable()

// ❌ Returning null from Optional method
public Optional<String> getName() {
    return null;  // WRONG! Return Optional.empty()
}
```

---

## Advanced Collectors - Complete Reference

### Basic Collectors

```java
// Collection collectors
.collect(toList())                              // → List<T>
.collect(toSet())                               // → Set<T>
.collect(toCollection(TreeSet::new))            // → Custom collection

// String operations
.collect(joining())                             // Concatenate
.collect(joining(", "))                         // With delimiter  
.collect(joining(", ", "[", "]"))              // With prefix/suffix
```

### Grouping and Partitioning

```java
// Simple grouping
Map<String, List<Person>> byDept = people.stream()
    .collect(groupingBy(Person::getDepartment));

// Partitioning (binary grouping)
Map<Boolean, List<Integer>> evenOdd = numbers.stream()
    .collect(partitioningBy(n -> n % 2 == 0));

// Multi-level grouping
Map<String, Map<String, List<Person>>> byDeptAndCity = people.stream()
    .collect(groupingBy(Person::getDepartment,
             groupingBy(Person::getCity)));
```

### Downstream Collectors

```java
// Count by group
Map<String, Long> countByDept = people.stream()
    .collect(groupingBy(Person::getDepartment, counting()));

// Sum by group
Map<String, Integer> totalSalaryByDept = people.stream()
    .collect(groupingBy(Person::getDepartment,
             summingInt(Person::getSalary)));

// Max/Min by group
Map<String, Optional<Person>> oldestByDept = people.stream()
    .collect(groupingBy(Person::getDepartment,
             maxBy(comparing(Person::getAge))));

// Collect to different collection type
Map<String, Set<String>> namesByDept = people.stream()
    .collect(groupingBy(Person::getDepartment,
             mapping(Person::getName, toSet())));
```

### Statistical Collectors

```java
// Counting
long count = stream.collect(counting());

// Numeric statistics
IntSummaryStatistics stats = people.stream()
    .collect(summarizingInt(Person::getAge));
System.out.println(stats.getAverage());
System.out.println(stats.getMax());

// Custom reduction
Optional<Person> oldest = people.stream()
    .collect(maxBy(comparing(Person::getAge)));
```

---

## Performance Optimization Guide

### Stream vs Loop Performance

```java
// Simple operations - traditional loops often faster
List<Integer> numbers = getNumbers();

// Traditional loop (often faster for simple ops)
int sum = 0;
for (Integer number : numbers) {
    sum += number;
}

// Stream (more readable, but overhead for simple ops)
int sum = numbers.stream().mapToInt(Integer::intValue).sum();

// Complex operations - streams shine
List<Person> result = people.stream()
    .filter(p -> p.getAge() > 18)
    .filter(p -> p.getDepartment().equals("Engineering"))
    .sorted(comparing(Person::getName))
    .collect(toList());
```

### Primitive Streams (Avoid Boxing)

```java
List<Integer> numbers = getNumbers();

// ❌ Boxing overhead
int sum = numbers.stream()
                 .mapToInt(Integer::intValue)
                 .sum();

// ✅ Use primitive streams
IntStream.range(1, 1000)           // Already primitive
         .filter(n -> n % 2 == 0)
         .sum();

// ✅ Direct primitive operations
int[] array = {1, 2, 3, 4, 5};
int sum = Arrays.stream(array).sum();  // No boxing
```

### Lazy Evaluation Benefits

```java
// Only processes until condition met
Optional<String> found = veryLargeList.stream()
    .filter(this::expensiveCheck)     // Only called until first match
    .findFirst();                     // Short-circuits

// Prefer specific operations over general ones
boolean hasMatch = stream.anyMatch(predicate);  // Better than:
boolean hasMatch = stream.filter(predicate).findFirst().isPresent();
```

---

## Real-World Use Cases & Scenarios

### Data Processing Pipeline

```java
public class OrderProcessor {
    public List<OrderSummary> processOrders(List<Order> orders) {
        return orders.stream()
            .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
            .filter(order -> order.getAmount().compareTo(BigDecimal.ZERO) > 0)
            .map(this::toOrderSummary)
            .sorted(comparing(OrderSummary::getDate).reversed())
            .collect(toList());
    }
    
    public Map<String, BigDecimal> getTotalSalesByProduct(List<Order> orders) {
        return orders.stream()
            .flatMap(order -> order.getItems().stream())
            .collect(groupingBy(
                OrderItem::getProductName,
                reducing(BigDecimal.ZERO, OrderItem::getPrice, BigDecimal::add)
            ));
    }
}
```

### File Processing

```java
public class LogAnalyzer {
    public Map<String, Long> analyzeErrorLogs(Path logFile) throws IOException {
        try (Stream<String> lines = Files.lines(logFile)) {
            return lines
                .filter(line -> line.contains("ERROR"))
                .map(this::extractErrorType)
                .filter(Objects::nonNull)
                .collect(groupingBy(identity(), counting()));
        }
    }
    
    public List<String> findSuspiciousIPs(Path accessLog) throws IOException {
        try (Stream<String> lines = Files.lines(accessLog)) {
            return lines
                .parallel()                          // Large files
                .map(this::parseLogEntry)
                .filter(Objects::nonNull)
                .collect(groupingBy(LogEntry::getIp, counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1000)  // > 1000 requests
                .map(Map.Entry::getKey)
                .collect(toList());
        }
    }
}
```

### Data Validation & Transformation

```java
public class UserService {
    public ValidationResult validateUsers(List<User> users) {
        List<String> errors = new ArrayList<>();
        
        List<User> validUsers = users.stream()
            .peek(user -> {
                if (user.getEmail() == null || !user.getEmail().contains("@")) {
                    errors.add("Invalid email for user: " + user.getName());
                }
            })
            .filter(user -> user.getEmail() != null && user.getEmail().contains("@"))
            .filter(user -> user.getAge() >= 18)
            .map(this::normalizeUser)
            .collect(toList());
            
        return new ValidationResult(validUsers, errors);
    }
    
    public Optional<User> findUserByEmail(String email) {
        return users.stream()
            .filter(user -> user.getEmail().equalsIgnoreCase(email))
            .findFirst();
    }
}
```

### Complex Aggregations

```java
public class SalesAnalytics {
    // Multi-level grouping with custom collectors
    public Map<String, Map<Integer, List<Sale>>> salesByRegionAndYear(List<Sale> sales) {
        return sales.stream()
            .collect(groupingBy(
                Sale::getRegion,
                groupingBy(sale -> sale.getDate().getYear())
            ));
    }
    
    // Top performers by region
    public Map<String, Optional<Salesperson>> topPerformerByRegion(List<Sale> sales) {
        return sales.stream()
            .collect(groupingBy(
                Sale::getRegion,
                collectingAndThen(
                    groupingBy(Sale::getSalesperson, summingDouble(Sale::getAmount)),
                    map -> map.entrySet().stream()
                              .max(comparing(Map.Entry::getValue))
                              .map(Map.Entry::getKey)
                )
            ));
    }
}
```

---

## Common Interview Questions - Complete Answers

### Technical Comparison Questions

**Q: "What's the difference between map() and flatMap()?"**
**A:** "map() performs 1:1 transformations - each input element produces exactly one output element. flatMap() flattens
nested structures - each input element can produce zero or more output elements which are then flattened into a single
stream.

Example: If you have `List<List<String>>` and use `map(List::size)`, you get `Stream<Integer>` with the sizes. But with
`flatMap(List::stream)`, you get `Stream<String>` with all the individual strings."

**Q: "Explain lazy evaluation in streams"**
**A:** "Streams use lazy evaluation - intermediate operations like filter(), map(), sorted() don't execute immediately.
They build a pipeline of operations. Only when a terminal operation like collect(), forEach(), or findFirst() is called
does the pipeline execute. This allows for optimizations like short-circuiting (stopping early when condition is met)
and fusion (combining operations for better performance)."

**Q: "When should you use parallel streams?"**
**A:** "Use parallel streams for:

- Large datasets (typically >10,000 elements)
- CPU-intensive operations (complex calculations, not I/O)
- Stateless operations (no shared mutable state)
- When you have multiple CPU cores available

Avoid for:

- Small datasets (<1,000 elements) - overhead outweighs benefits
- I/O-bound operations - threads will just wait
- Operations with side effects or shared state - race conditions
- When ordering is critical (unless using forEachOrdered)"

**Q: "Why use Optional instead of null?"**
**A:** "Optional provides several benefits:

1. **Explicit contracts** - method signatures clearly indicate potential absence
2. **Null safety** - prevents NullPointerException through functional chaining
3. **Functional composition** - enables chaining with map(), filter(), flatMap()
4. **Forces handling** - developers must explicitly deal with empty cases
5. **Better code readability** - intent is clearer than null checks"

### Practical Coding Questions

**Q: "How would you find the top 3 highest-paid employees in each department?"**

```java
public Map<String, List<Employee>> topThreeByDepartment(List<Employee> employees) {
    return employees.stream()
        .collect(groupingBy(
            Employee::getDepartment,
            collectingAndThen(
                toList(),
                list -> list.stream()
                           .sorted(comparing(Employee::getSalary).reversed())
                           .limit(3)
                           .collect(toList())
            )
        ));
}
```

**Q: "Write code to safely navigate nested object properties"**

```java
public Optional<String> getUserCountryCode(Long userId) {
    return findUser(userId)                    // Optional<User>
        .flatMap(User::getAddress)             // Optional<Address>
        .flatMap(Address::getCountry)          // Optional<Country>
        .map(Country::getCode);                // Optional<String>
}
```

### Edge Case Questions

**Q: "What happens if you use a stream twice?"**
**A:** "Streams can only be consumed once. After a terminal operation, the stream is closed. Attempting to use it again
throws IllegalStateException. You need to create a new stream for each use."

```java
Stream<String> stream = list.stream();
stream.count();        // Terminal operation
stream.forEach(...);  // IllegalStateException!

// Correct approach
Supplier<Stream<String>> streamSupplier = () -> list.stream();
long count = streamSupplier.get().count();
streamSupplier.get().forEach(...);  // New stream each time
```

**Q: "How do you handle exceptions in streams?"**

```java
// Problem: Checked exceptions in lambdas
// stream.map(file -> Files.readString(file));  // Won't compile

// Solution 1: Wrapper method
public String readFileUnchecked(Path file) {
    try {
        return Files.readString(file);
    } catch (IOException e) {
        throw new UncheckedIOException(e);
    }
}
stream.map(this::readFileUnchecked)

// Solution 2: Optional wrapper
public Optional<String> safeReadFile(Path file) {
    try {
        return Optional.of(Files.readString(file));
    } catch (IOException e) {
        return Optional.empty();
    }
}
stream.map(this::safeReadFile).flatMap(Optional::stream)
```

---

## Memory Tricks & Mnemonics

### SOLID for Streams
- **S**tream creation (`.stream()`, `.of()`, `.generate()`)
- **O**perations intermediate (`.filter()`, `.map()`, `.sorted()`)
- **L**azy evaluation (intermediate ops don't execute until terminal)
- **I**mmutable (streams don't modify source)
- **D**one once (streams are consumable, can't reuse)

### PEACE for Optional

- **P**resent: Check with `isPresent()`, act with `ifPresent()`
- **E**mpty: Handle with `orElse()`, `orElseGet()`, `orElseThrow()`
- **A**void: Never use for fields or parameters
- **C**hain: Transform with `map()`, filter with `filter()`, flatten with `flatMap()`
- **E**xplicit: Makes null-handling visible in API

### Four Method References (SICC)

1. **S**tatic: `Math::abs`, `Integer::parseInt`
2. **I**nstance (bound): `System.out::println`, `obj::method`
3. **I**nstance (unbound): `String::length`, `Person::getName`
4. **C**onstructor: `ArrayList::new`, `String[]::new`

### Performance Tips (PABLO)

- **P**rimitive streams avoid boxing (`IntStream` vs `Stream<Integer>`)
- **A**ppropriate operations (`anyMatch()` vs `filter().findFirst().isPresent()`)
- **B**ig data for parallel streams (>10k elements)
- **L**azy evaluation stops early (`findFirst()` short-circuits)
- **O**ptimize collectors (use specific ones, not general `collect()`)

---

## Critical Pitfalls - Interview Red Flags

### Stream Misuse

```java
// ❌ Using stream more than once
Stream<String> stream = list.stream();
stream.count();
stream.forEach(...);  // Exception!

// ❌ Modifying source during iteration
list.stream().forEach(item -> list.remove(item));  // ConcurrentModificationException

// ❌ Side effects in filter/map
list.stream().filter(item -> {
    sideEffectList.add(item);  // BAD - side effect in filter
    return item.length() > 5;
});
```

### Optional Misuse

```java
// ❌ Optional as field/parameter
class Person {
    Optional<String> name;  // WRONG!
}
void setName(Optional<String> name) { }  // WRONG!

// ❌ isPresent() + get()
if (opt.isPresent()) {
    return opt.get();  // WRONG! Use orElse() or map()
}

// ❌ Optional.of() with null
Optional.of(possiblyNull);  // NPE! Use ofNullable()
```

### Parallel Stream Pitfalls

```java
// ❌ Race conditions
List<String> results = new ArrayList<>();
stream.parallel().forEach(results::add);  // Not thread-safe!

// ❌ Shared mutable state
int counter = 0;
stream.parallel().forEach(item -> counter++);  // Race condition!

// ❌ Using parallel for I/O operations
files.parallelStream()
     .map(this::readFile)  // I/O bound - don't parallelize
```

### Lambda Capture Issues

```java
// ❌ Modifying captured variables
int count = 0;
list.forEach(item -> count++);  // Won't compile - not effectively final

// ❌ Capturing mutable objects incorrectly
List<String> captured = new ArrayList<>();
// If 'captured' is modified later, lambda behavior is undefined
```

---

## Final Interview Success Tips

### Before the Interview

1. **Practice whiteboard coding** - write stream operations without IDE
2. **Memorize the comparison tables** - especially lambda vs anonymous classes
3. **Know the performance implications** - when streams help vs hurt
4. **Understand the "why"** behind each choice, not just the "how"

### During Technical Questions

1. **Start simple** - basic stream operation, then add complexity
2. **Think out loud** - explain your reasoning
3. **Consider edge cases** - empty streams, null values, exceptions
4. **Mention performance** - show awareness of trade-offs
5. **Use method references** when appropriate - shows advanced knowledge

### Code Review Questions

Be ready to critique code like:

```java
// What's wrong with this code?
Optional<String> result = list.stream()
    .filter(s -> s.length() > 5)
    .findFirst();
if (result.isPresent()) {
    return result.get().toUpperCase();
} else {
    return "DEFAULT";
}

// Better version:
return list.stream()
    .filter(s -> s.length() > 5)
    .findFirst()
    .map(String::toUpperCase)
    .orElse("DEFAULT");
```

### Advanced Topics to Mention

- **Custom Collectors** - show you understand the framework deeply
- **Spliterator** - for custom parallel processing
- **Exception handling** in streams - practical knowledge
- **Performance profiling** - real-world experience

---

Remember: **Confidence comes from deep understanding, not memorization.**

## You've Got This! 🚀💪

Good luck with your checkpoint interview!