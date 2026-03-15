# accord-collections
Java collections with declared contracts. No null surprises, no hidden threading, no implicit behavior.

---

## Installation

### Maven Central (recommended)

No extra repository or authentication needed.

```xml
<dependency>
    <groupId>io.github.ashkrit</groupId>
    <artifactId>accord-collections</artifactId>
    <version>1.0.0</version>
</dependency>
```

### GitHub Packages

Requires a GitHub personal access token (PAT) with `read:packages` scope.

**1. Add the repository**

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/ashkrit/accord-collections</url>
    </repository>
</repositories>
```

**2. Add the dependency**

```xml
<dependency>
    <groupId>io.github.ashkrit</groupId>
    <artifactId>accord-collections</artifactId>
    <version>1.0.0</version>
</dependency>
```

**3. Authenticate** — add to `~/.m2/settings.xml`:

```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>YOUR_GITHUB_USERNAME</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

Generate a token at **GitHub → Settings → Developer settings → Personal access tokens** with the `read:packages` scope.

---

## MapPlus

`MapPlus<K, V>` extends `java.util.Map` with five live-view operations and a strict null contract.

### Null contract

| Operation | Behaviour |
|---|---|
| `put(null, v)` | `NullPointerException` |
| `put(k, null)` | `NullPointerException` |
| `get(null)` | returns `null` (never stored) |
| `containsKey(null)` | returns `false` |
| any view factory with `null` arg | `NullPointerException` |

### Implementations

| Class | Backing store | Key order |
|---|---|---|
| `StandardMapPlus<K,V>` | `LinkedHashMap` | insertion |
| `SortedMapPlus<K extends Comparable<K>,V>` | `TreeMap` | ascending or descending |

---

## Basic usage

```java
MapPlus<String, Integer> scores = new StandardMapPlus<>();
scores.put("alice", 95);
scores.put("bob",   82);
scores.put("carol", 88);

scores.get("alice");  // 95
scores.get(null);     // null  (never throws)
scores.put(null, 0);  // NullPointerException: key must not be null
```

---

## SortedMapPlus

```java
SortedMapPlus<String, Integer> asc = new SortedMapPlus<>(Ordering.ASCENDING);
asc.put("banana", 2);
asc.put("apple",  1);
asc.put("cherry", 3);
// keySet: [apple, banana, cherry]

SortedMapPlus<String, Integer> desc = new SortedMapPlus<>(Ordering.DESCENDING);
desc.put("banana", 2);
desc.put("apple",  1);
desc.put("cherry", 3);
// keySet: [cherry, banana, apple]
```

---

## view(Ordering) — sorted iteration

Returns a live view whose iteration order is sorted, while the backing map is unchanged.

```java
StandardMapPlus<String, Integer> map = new StandardMapPlus<>();
map.put("c", 3);
map.put("a", 1);
map.put("b", 2);

map.view(Ordering.ASCENDING).keySet();   // [a, b, c]
map.view(Ordering.DESCENDING).keySet();  // [c, b, a]
map.keySet();                            // [c, a, b]  — unchanged
```

---

## project(Function) — transform values on read

Every `get()` applies the function to the backing value. No copy is made.

```java
StandardMapPlus<String, Double> prices = new StandardMapPlus<>();
prices.put("apple",  1.20);
prices.put("banana", 0.50);

MapPlus<String, String> formatted = prices.project(p -> String.format("$%.2f", p));
formatted.get("apple");   // "$1.20"
formatted.get("banana");  // "$0.50"
```

```java
MapPlus<String, Integer> stock = new StandardMapPlus<>();
stock.put("apple",  150);
stock.put("banana", 8);
stock.put("cherry", 0);

MapPlus<String, String> level = stock.project(n -> n == 0 ? "OUT" : n < 10 ? "LOW" : "OK");
level.get("apple");   // "OK"
level.get("banana");  // "LOW"
level.get("cherry");  // "OUT"
```

---

## filter(Predicate) — restrict visible entries

Only entries whose value satisfies the predicate are accessible.

```java
StandardMapPlus<String, Integer> scores = new StandardMapPlus<>();
scores.put("alice", 95);
scores.put("bob",   62);
scores.put("carol", 78);
scores.put("dave",  45);

MapPlus<String, Integer> passing = scores.filter(s -> s >= 70);
passing.keySet();              // [alice, carol]
passing.containsKey("bob");    // false
passing.size();                // 2
```

---

## then(MapPlus) — chain two maps

Uses this map's values as keys into `next`, yielding a direct key→final-value lookup.

```java
StandardMapPlus<String, String> userRoles = new StandardMapPlus<>();
userRoles.put("alice", "admin");
userRoles.put("bob",   "editor");
userRoles.put("carol", "viewer");

StandardMapPlus<String, Integer> rolePermissions = new StandardMapPlus<>();
rolePermissions.put("admin",  3);
rolePermissions.put("editor", 2);
rolePermissions.put("viewer", 1);

MapPlus<String, Integer> userPermissions = userRoles.then(rolePermissions);
userPermissions.get("alice");  // 3
userPermissions.get("carol");  // 1
```

---

## merge(MapPlus, MergeStrategy) — combine two maps

Three strategies control what happens when both maps contain the same key.

```java
StandardMapPlus<String, String> defaults = new StandardMapPlus<>();
defaults.put("color", "blue");
defaults.put("font",  "Arial");

StandardMapPlus<String, String> overrides = new StandardMapPlus<>();
overrides.put("color",  "red");   // conflict
overrides.put("weight", "bold");  // new key

// PREFER_LEFT — defaults win
MapPlus<String, String> leftWins = defaults.merge(overrides, MergeStrategy.PREFER_LEFT);
leftWins.get("color");   // "blue"
leftWins.get("weight");  // "bold"

// PREFER_RIGHT — overrides win
MapPlus<String, String> rightWins = defaults.merge(overrides, MergeStrategy.PREFER_RIGHT);
rightWins.get("color");  // "red"

// THROW — conflict throws on access (not on construction)
MapPlus<String, String> strict = defaults.merge(overrides, MergeStrategy.THROW);
strict.get("font");   // "Arial"  — no conflict, fine
strict.get("color");  // IllegalStateException: Key conflict: color
```

---

## Live views

All five view operations hold a reference to the backing map — no copy is taken.
Mutations to the backing map are visible immediately through every view.

```java
StandardMapPlus<String, Integer> inventory = new StandardMapPlus<>();
inventory.put("apples",  50);
inventory.put("bananas", 3);

MapPlus<String, Integer> lowStock  = inventory.filter(qty -> qty < 10);
MapPlus<String, String>  formatted = inventory.project(qty -> qty + " units");

lowStock.keySet();           // [bananas]

inventory.put("apples",  2);   // apples drops to low stock
inventory.put("bananas", 40);  // bananas recovers

lowStock.keySet();             // [apples]   — reflects the change
formatted.get("apples");       // "2 units"  — reflects the change
```

All views are **read-only**. Calling `put`, `remove`, or `clear` on any view throws `UnsupportedOperationException`.

---

## Composing views

Views can be chained — each step returns a new live view.

```java
StandardMapPlus<String, Integer> scores = new StandardMapPlus<>();
scores.put("carol", 78);
scores.put("alice", 95);
scores.put("dave",  45);
scores.put("bob",   62);

MapPlus<String, String> result = scores
    .filter(s -> s >= 70)          // passing only
    .view(Ordering.ASCENDING)      // alphabetical
    .project(s -> s + "%");        // format

result.entrySet().forEach(e -> System.out.println(e.getKey() + " → " + e.getValue()));
// alice → 95%
// carol → 78%
```

---

## Static factories

```java
MapPlus<String, Integer> empty = MapPlus.empty();

MapPlus<String, Integer> map = MapPlus.of(
    Map.entry("x", 10),
    Map.entry("y", 20)
);
```
