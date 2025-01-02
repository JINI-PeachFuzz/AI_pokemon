package org.koreait.dl.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Lazy
@Service
@Profile("dl")
public class SentimentService {

    @Value("${python.run.path}")
    private String runPath;

    @Value("${python.script2.path}")
    private String scriptPath;

    @Value("${python.bert.path}")
    private String bertPath;

    @Autowired
    private ObjectMapper om;

    public double[] predict(List<String> items) {
        try {
            String data = String.join("__", items); // 문자열결합

            ProcessBuilder builder = new ProcessBuilder(runPath, scriptPath + "naver.py", bertPath, data);
            Process process = builder.start();
            InputStream in = process.getInputStream();

            process.waitFor();

            return om.readValue(in.readAllBytes(), double[].class); // 더블형태로 반환값이 나올 거임

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
