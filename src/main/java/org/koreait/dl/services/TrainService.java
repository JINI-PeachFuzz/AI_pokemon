package org.koreait.dl.services;

import lombok.RequiredArgsConstructor;
import org.koreait.dl.entities.QTrainItem;
import org.koreait.dl.entities.TrainItem;
import org.koreait.dl.repositories.TrainItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.data.domain.Sort.Order.asc;

@Lazy
@Service
@Profile("dl") // dl이라는게 있으면 선별적으로 빈을 만들어서 하겠다.
@RequiredArgsConstructor
public class TrainService {

    private final TrainItemRepository repository;

    @Value("${python.run.path}")
    private String runPath;

    @Value("${python.script.path}")
    private String scriptPath;

    @Value("${python.data.url}")
    private String dataUrl;

    @Scheduled(cron="0 0 1 * * *") // 새벽 1시 마다 훈련
    public void process() {
        try {
            ProcessBuilder builder = new ProcessBuilder(runPath, scriptPath + "train.py", dataUrl + "?mode=ALL", dataUrl);
            // 전체데이터+학습데이터
            Process process = builder.start();
            int exitCode = process.waitFor();
            System.out.println(exitCode);

        } catch (Exception e) {}
    }

    // 훈련기록 기억
    public void log(TrainItem item) {
        repository.saveAndFlush(item);
    }


    public List<TrainItem> getList(boolean isAll) {


        if (isAll) {
            return repository.findAll(Sort.by(asc("createdAt")));
        } else {
            QTrainItem trainItem = QTrainItem.trainItem;
            return (List<TrainItem>)repository.findAll(trainItem.createdAt.after(LocalDateTime.of(LocalDate.now().minusDays(1L), LocalTime.of(0, 0, 0))), Sort.by(asc("createdAt")));
            // 0시0분0초 부터 / 하루치 데이터 지금날짜의 전날새벽 1시부터 돌린다고 해서 minusDays(1L)을 해야1시부터가 적용됨
        }
    }
}
