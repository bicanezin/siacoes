package br.edu.utfpr.dv.siacoes.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentTest {

    @Test
    void getCampus() {
        Department d = new Department();
        String campusName = "Cornélio Procópio";

        d.setCampus(campusName);
        String result = d.getCampus();

        assertEquals(campusName, result);
    }
}