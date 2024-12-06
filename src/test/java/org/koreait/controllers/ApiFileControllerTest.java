package org.koreait.file.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.repositories.FileInfoRepository;
import org.koreait.file.services.FileDeleteService;
import org.koreait.file.services.FileInfoService;
import org.koreait.member.constants.Gender;
import org.koreait.member.controllers.RequestJoin;
import org.koreait.member.services.MemberUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ActiveProfiles({"default", "test"})
@AutoConfigureMockMvc
public class ApiFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileInfoRepository repository;

    @Autowired
    private MemberUpdateService updateService;

    @Autowired
    private FileInfoService infoService;

    @Autowired
    private FileDeleteService deleteService;

    @BeforeEach
    void setup() {
        //mockMvc = MockMvcBuilders.standaloneSetup(ApiFileController.class).build();
        //이거는 단일 테스트 전부 불러와서 해야한다면 위에 Autowired를 사용해서 해야함

        RequestJoin form = new RequestJoin();
        form.setEmail("user01@test.org");
        form.setPassword("_aA123456");
        form.setGender(Gender.MALE);
        form.setBirthDt(LocalDate.now().minusYears(20));
        form.setName("이이름");
        form.setNickName("이이름");
        form.setZipCode("00000");
        form.setAddress("주소!");

        updateService.process(form);
    }


    @Test
    //@WithMockUser(username = "user01@test.org", authorities = "USER") // 가짜로 넣어논거 들어가는지 확인해볼려고
    @WithUserDetails(value = "user01@test.org", userDetailsServiceBeanName = "memberInfoService", setupBefore = TestExecutionEvent.TEST_EXECUTION) // 실제 유저 데이터를 사용해서 해보고 싶을 떄 사용/ 단, 하나이상 회원가입을 한 후에 사용해야함
    void test1() throws Exception {
        /**
         * MockMultipartFile
         */
        MockMultipartFile file1 = new MockMultipartFile("file", "test1.png", MediaType.IMAGE_PNG_VALUE, new byte[] {1, 2, 3});
        MockMultipartFile file2 = new MockMultipartFile("file", "test2.png", MediaType.IMAGE_PNG_VALUE, new byte[] {1, 2, 3});

        mockMvc.perform(multipart("/api/file/upload")
                        .file(file1)
                        .file(file2)
                        .param("gid", "testgid")
                        .param("location", "testlocation")
                        .with(csrf().asHeader()))
                .andDo(print());


        //Thread.sleep(5000); // 5초지연 / sleep은 실행중인 스레드가 지연됨

        List<FileInfo> items = infoService.getList("testgid", null, null);
        for (FileInfo item : items) {
            System.out.println(item.getCreatedBy());
        }
    }

    @Test
    void test2() {
        FileInfo item = infoService.get(1L);
        System.out.println(item);

        List<FileInfo> items = infoService.getList("testgid", null, null);
        // 여기에 null들이 없으면 기본값이 ALL임
        items.forEach(System.out::println);

    }

    @Test
    void test3() {
        //FileInfo item = deleteService.delete(1L);
        //System.out.println(item);
        List<FileInfo> items = deleteService.deletes("testgid", "testlocation");
        items.forEach(System.out::println);
    }
}