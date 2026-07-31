# Counter Feature File

## Scenario: Create Counter with Max Value

### Given
- A Counter is created with maxValue 5

### When
- No actions taken

### Then
- isReady should return false

## Scenario: Tick the Counter

### Given
- A Counter is created with maxValue 5

### When
- tick() is called 4 times

### Then
- isReady should return false

## Scenario: Counter reaches zero

### Given
- A Counter is created with maxValue 5

### When
- tick() is called 5 times

### Then
- isReady should return true

## Scenario: Counter overreaches zero

### Given
- A Counter is created with maxValue 5

### When
- tick() is called 6 times

### Then
- isReady should return true

## Scenario: Reset the Counter

### Given
- A Counter is created with maxValue 5
- tick() is called 3 times

### When
- reset() is called
- tick() is called 4 times

### Then
- isReady should return false

## Scenario: Reset the Counter and Tick to Ready Again

### Given
- A Counter is created with maxValue 5
- tick() is called 3 times

### When
- reset() is called
- tick() is called 5 times

### Then
- isReady should return true

## Scenario: Create Counter with Initial Value

### Given
- A Counter is created with initValue 3 and maxValue 5

### When
- No actions taken

### Then
- isReady should return false

## Scenario: Tick until zero with initial value

### Given
- A Counter is created with initValue 3 and maxValue 5

### When
- tick() is called 3 times

### Then
- isReady should return true

## Scenario: Reset after ticking

### Given
- A Counter is created with initValue 3 and maxValue 5
- tick() is called 2 times

### When
- reset() is called
- tick() is called 4 times

### Then
- isReady should return false
