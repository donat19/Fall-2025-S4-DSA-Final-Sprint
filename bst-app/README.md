# BST Application - Binary Search Tree Web Application

## Project Description

This is a Spring Boot web application that allows users to:
- Enter numbers through an HTML form
- Build a Binary Search Tree (BST) from those numbers
- View the tree in JSON format
- Save trees to a database
- View history of previously created trees

## Technologies

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** - for database operations
- **Thymeleaf** - template engine for HTML pages
- **H2 Database** - embedded in-memory database
- **Jackson** - for tree serialization to JSON
- **JUnit 5** - for unit testing
- **Maven** - build system

## Project Structure

```
bst-app/
├── src/
│   ├── main/
│   │   ├── java/com/bstapp/
│   │   │   ├── BstApplication.java          # Main application class
│   │   │   ├── controller/
│   │   │   │   └── BstController.java       # HTTP controller
│   │   │   ├── service/
│   │   │   │   ├── BstService.java          # BST business logic
│   │   │   │   └── BstNode.java             # Tree node
│   │   │   ├── model/
│   │   │   │   └── BstTree.java             # JPA entity for DB
│   │   │   └── repository/
│   │   │       └── BstTreeRepository.java   # Database repository
│   │   └── resources/
│   │       ├── application.properties       # Configuration
│   │       └── templates/
│   │           ├── enter-numbers.html       # Number input page
│   │           └── previous-trees.html      # History page
│   └── test/java/com/bstapp/
│       ├── service/
│       │   └── BstServiceUnitTest.java      # BST logic unit tests
│       ├── controller/
│       │   └── BstControllerTest.java       # Controller tests
│       └── repository/
│           └── BstTreeRepositoryTest.java   # Repository tests
└── pom.xml                                   # Maven configuration
```

## Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/` | Redirect to `/enter-numbers` |
| GET | `/enter-numbers` | HTML page for entering numbers |
| POST | `/process-numbers` | Process numbers, build BST, return JSON |
| GET | `/previous-trees` | HTML page with tree history |
| GET | `/api/trees` | REST API - all trees in JSON format |
| GET | `/h2-console` | H2 database console |

## Running the Application

```bash
cd bst-app
mvn spring-boot:run
```

After starting, the application is available at: http://localhost:8080

## H2 Database

- **Console URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:bstdb`
- **Username**: `sa`
- **Password**: (empty)

## Usage Example

1. Open http://localhost:8080/enter-numbers
2. Enter numbers: `7, 3, 9, 1, 4`
3. Click "Build Tree"
4. Receive JSON representation of the tree:

```json
{
  "value": 7,
  "left": {
    "value": 3,
    "left": { "value": 1 },
    "right": { "value": 4 }
  },
  "right": { "value": 9 }
}
```

Visual representation of the tree:
```
       7
      / \
     3   9
    / \
   1   4
```

---

# Testing Documentation

## Test Overview

The project contains **21 unit tests**, divided into three categories:

| Category | File | Number of Tests |
|----------|------|-----------------|
| BST Logic | `BstServiceUnitTest.java` | 10 tests |
| Controller | `BstControllerTest.java` | 6 tests |
| Repository | `BstTreeRepositoryTest.java` | 5 tests |

## Running Tests

```bash
cd bst-app
mvn test
```

---

## 1. BST Logic Tests (BstServiceUnitTest)

These tests verify the correctness of Binary Search Tree construction.

### Test 1: testBstConstruction
**Purpose**: Verify correct BST construction from a list of numbers.

**Input**: `[7, 3, 9, 1, 4]`

**Expected tree**:
```
       7
      / \
     3   9
    / \
   1   4
```

**Assertions**:
- Root = 7
- Left child of root = 3
- Right child of root = 9
- Left child of node 3 = 1
- Right child of node 3 = 4
- Leaf nodes have no children

---

### Test 2: testBstPropertyMaintained
**Purpose**: Ensure the BST property is maintained for all nodes.

**BST Property**: For each node, all values in the left subtree are smaller, and all values in the right subtree are larger.

**Input**: `[50, 30, 70, 20, 40, 60, 80]`

**Verification method**: Recursive traversal checking bounds (min, max) for each node.

---

### Test 3: testSingleElementTree
**Purpose**: Verify creation of a tree with a single element.

**Input**: `[42]`

**Expected result**:
- Root = 42
- Left child = null
- Right child = null

---

### Test 4: testEmptyListReturnsNull
**Purpose**: Verify handling of empty list.

**Input**: `[]`

**Expected result**: `null`

---

### Test 5: testNullListReturnsNull
**Purpose**: Verify handling of null input.

**Input**: `null`

**Expected result**: `null`

---

### Test 6: testDuplicatesNotInserted
**Purpose**: Ensure duplicates are not inserted into the tree.

**Input**: `[5, 3, 5, 7, 3, 5]`

**Expected tree**:
```
    5
   / \
  3   7
```

**Assertions**: Node 3 has no children (duplicates were skipped).

---

### Test 7: testAscendingOrderCreatesRightSkewedTree
**Purpose**: Verify creation of a degenerate tree with sorted input.

**Input**: `[1, 2, 3, 4, 5]`

**Expected tree** (right-skewed):
```
1
 \
  2
   \
    3
     \
      4
       \
        5
```

---

### Test 8: testNumberParsing
**Purpose**: Verify parsing of comma-separated number string.

**Input**: `"7, 3, 9, 1, 4"`

**Expected result**: `[7, 3, 9, 1, 4]`

---

### Test 9: testNumberParsingWithSpaces
**Purpose**: Verify parsing of space-separated number string.

**Input**: `"10 20 30 40"`

**Expected result**: `[10, 20, 30, 40]`

---

### Test 10: testInvalidNumberParsingThrowsException
**Purpose**: Verify handling of invalid input.

**Input**: `"1, 2, abc, 4"`

**Expected result**: `NumberFormatException`

---

## 2. Controller Tests (BstControllerTest)

Integration tests for HTTP routes using MockMvc.

### Test 1: testEnterNumbersPageLoads
**Purpose**: Verify the number input page loads correctly.

**Request**: `GET /enter-numbers`

**Assertions**:
- HTTP status: 200 OK
- View name: `enter-numbers`

---

### Test 2: testProcessNumbersSuccess
**Purpose**: Verify successful number processing.

**Request**: `POST /process-numbers`
```json
{"numbers": "7, 3, 9"}
```

**Assertions**:
- HTTP status: 200 OK
- Response body: Tree JSON

---

### Test 3: testProcessNumbersInvalidInput
**Purpose**: Verify handling of invalid input.

**Request**: `POST /process-numbers`
```json
{"numbers": "abc"}
```

**Assertions**:
- HTTP status: 400 Bad Request

---

### Test 4: testPreviousTreesPageLoads
**Purpose**: Verify the history page loads correctly.

**Request**: `GET /previous-trees`

**Assertions**:
- HTTP status: 200 OK
- View name: `previous-trees`
- Model contains `trees` attribute

---

### Test 5: testHomeRedirectsToEnterNumbers
**Purpose**: Verify redirect from root URL.

**Request**: `GET /`

**Assertions**:
- HTTP status: 3xx (redirect)
- Redirect URL: `/enter-numbers`

---

### Test 6: testApiTreesReturnsJson
**Purpose**: Verify REST API endpoint.

**Request**: `GET /api/trees`

**Assertions**:
- HTTP status: 200 OK
- Content-Type: `application/json`

---

## 3. Repository Tests (BstTreeRepositoryTest)

Database operation tests using @DataJpaTest.

### Test 1: testSaveAndFindTree
**Purpose**: Verify saving and retrieving a tree from the database.

**Actions**:
1. Create BstTree with inputNumbers and treeJson
2. Save to database
3. Retrieve via findAll()

**Assertions**:
- Record count = 1
- inputNumbers matches
- treeJson is not null
- createdAt is set automatically

---

### Test 2: testFindAllByOrderByCreatedAtDesc
**Purpose**: Verify sorting by creation date (newest first).

**Actions**:
1. Create tree1
2. Wait 10ms
3. Create tree2
4. Call findAllByOrderByCreatedAtDesc()

**Assertions**:
- First element = tree2 (newer)
- Second element = tree1

---

### Test 3: testAutoGeneratedId
**Purpose**: Verify auto-generation of ID.

**Actions**:
1. Create tree (id = null)
2. Save to database
3. Check id

**Assertions**:
- Before save: id = null
- After save: id > 0

---

### Test 4: testDeleteTree
**Purpose**: Verify record deletion.

**Actions**:
1. Save tree
2. Verify count = 1
3. Delete tree
4. Check count

**Assertions**:
- After deletion: count = 0

---

### Test 5: testFindById
**Purpose**: Verify search by ID.

**Actions**:
1. Save tree
2. Get generated ID
3. Call findById(id)

**Assertions**:
- Optional.isPresent() = true
- inputNumbers matches

---

## Test Results

```
[INFO] Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

All 21 tests pass successfully ✓

---

## BST Construction Algorithm

### Insertion Algorithm Description

```java
public BstNode insert(BstNode node, int value) {
    // Base case: found an empty position
    if (node == null) {
        return new BstNode(value);
    }
    
    // Recursive case
    if (value < node.getValue()) {
        // Value is smaller -> go left
        node.setLeft(insert(node.getLeft(), value));
    } else if (value > node.getValue()) {
        // Value is larger -> go right
        node.setRight(insert(node.getRight(), value));
    }
    // If value == node.getValue() -> skip (no duplicates)
    
    return node;
}
```

### Complexity
- **Average case**: O(log n) for insertion
- **Worst case**: O(n) for degenerate tree

---

## Database Schema

### Table: bst_trees

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (PK, AUTO) | Unique identifier |
| input_numbers | VARCHAR(255) | User-entered numbers |
| tree_json | TEXT | JSON representation of tree |
| created_at | TIMESTAMP | Record creation time |

---

## Author

Student project for Data Structures and Algorithms (DSA) course
Fall 2025, Semester 4
