package cia.arkrypto.se.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point {
    private int x, y;

    public String toString(){
        return "(" + x + ", " + y + ")";
    }
}
