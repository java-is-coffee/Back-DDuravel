package javaiscoffee.polaroad.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(MemberController.class)
class MemberControllerTest {
    @Autowired
    private MemberController memberController;

}