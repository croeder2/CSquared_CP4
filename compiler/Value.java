/**
 * Description: This class acts as a Wrapper for data types and values in the compiler
 * @author Claire Roeder
 * @author Cameron Herbert
 * Programming Project 4
 * CS322 - Compiler Construction
 * Fall 2021
 */
public class Value{

public String declareType; //either int or string
public String value; //value of variable
public String name; //variable name

/**
 * Value Object Constructor
 */
public Value(){
    declareType = "";
    value = "";
    name = "";
}

/**
 * Value Object with parameters passed in
 * @param declareType
 * @param value
 * @param name
 */
public Value(String declareType, String value, String name){
    this.declareType = declareType;
    this.value = value;
    this.name = name;
}

/**
 * Getters and Setters
 * @return
 */

public String getDeclareType(){
    return declareType;
}
public void setDeclareType(){
    this.declareType = declareType;
}
public String getValue(){
    return value;
}
public void setValue(){
    this.value = value;
}
public String getName(){
    return name;
}
public void setName(){
    this.name = name;
}

}