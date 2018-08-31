/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package barchartprinter;

/**
 *
 * @author nithammer
 */
public class MyString implements Comparable<MyString>{

    private String content;
    
    public MyString(String value){
        content = value;
    }
    
    public void setValue(String value){
        content = value;
    }
    
    public String getValue(){
        return content;
    }
    
    @Override
    public int compareTo(MyString o) {
        try{
            int x = Integer.parseInt(getValue());
            int y = Integer.parseInt(o.getValue());
            if (y==x) return 0;
            if (x>y) return 1;
            if (y>x) return -1;
        } catch (NumberFormatException nfe){
            return getValue().compareTo(o.getValue());
        }
        return 0;
    }
    
}
