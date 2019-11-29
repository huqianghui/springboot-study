package com.hu.study.chapter13;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(GoodByeController.class)
public class GoodByeControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private HelloController helloController;

    @Before
    public void setUp() {

    }

    @Test
    public void goodbyeAfterHello() throws Exception {
        given(this.helloController.sayHello()).willReturn("hello test.");
        mvc.perform(MockMvcRequestBuilders.get("/goodbye").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("hello test. goodbye.")));
    }

}
