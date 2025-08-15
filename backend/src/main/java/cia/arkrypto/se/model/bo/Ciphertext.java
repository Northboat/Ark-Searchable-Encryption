package cia.arkrypto.se.model.bo;

import it.unisa.dia.gas.jpbc.Element;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Ciphertext {
    Element[] y;
    Element C;
    Element D;
    Element[] E;
}
