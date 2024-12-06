package org.koreait.file.services;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.entities.QFileInfo;
import org.koreait.file.exceptions.FileNotFoundException;
import org.koreait.file.repositories.FileInfoRepository;
import org.koreait.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static org.springframework.data.domain.Sort.Order.asc;

@Lazy
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class) // fileUrl과 path 가져올려고
public class FileInfoService {
    private final FileInfoRepository infoRepository;

    private final FileProperties properties;

    private final HttpServletRequest request;

    public FileInfo get(Long seq) {
        FileInfo item = infoRepository.findById(seq).orElseThrow(FileNotFoundException::new);

        addInfo(item); // 추가 정보 처리 / 아래 개별조회를 만들어서 추가로 Long seq 또 안해주면 무한 루프됨

        return item;
    }

    public List<FileInfo> getList(String gid, String location, FileStatus status) {
        status = Objects.requireNonNullElse(status, FileStatus.ALL);
        // ALL 일때는 조건추가X
        QFileInfo fileInfo = QFileInfo.fileInfo; // 검색
        BooleanBuilder andBuilder = new BooleanBuilder(); // 여러개라서 BooleanBuilder를 사용한거 / 미리 추가해논거
        andBuilder.and(fileInfo.gid.eq(gid)); // 필수

        if (StringUtils.hasText(location)) { // 선택
            andBuilder.and(fileInfo.location.eq(location));
        }

        // 파일 작업 완료 상태
        if (status != FileStatus.ALL) {
            andBuilder.and(fileInfo.done.eq(status == FileStatus.DONE));
        }

        List<FileInfo> items = (List<FileInfo>) infoRepository.findAll(andBuilder, Sort.by(asc("createdAt"))); // Builder는 하나일때

        // 추가 정보 처리
        items.forEach(this::addInfo);

        return items;
    }

    public List<FileInfo> getList(String gid, String location) {
        return getList(gid, location, FileStatus.DONE);
    }

    public List<FileInfo> getList(String gid) { // 파일 그룹작업 완료된 파일
        return getList(gid, null);
    }

    /**
     * 추가 정보 처리 // fileUrl과 path에 관해서
     */
    public void addInfo(FileInfo item) {
        // filePath - 서버에 올라간 실제 경로(다운로드, 삭제시 활용...)
        item.setFilePath(getFilePath(item));
        // fileUrl - 접근할 수 있는 주소(브라우저)
        item.setFileUrl(getFileUrl(item));
    }
    // 개별로 사용하기도 해서(개별 조회) 아래에 따로 또 해준거
    public String getFilePath(FileInfo item) {
        Long seq = item.getSeq();
        String extension = Objects.requireNonNullElse(item.getExtension(), ""); // 오류 방지를 위해서 기본값을 넣었음 ""
        return String.format("%s%s/%s", properties.getPath(), getFolder(seq), seq + extension);
    }

    public String getFilePath(Long seq) {
        FileInfo item = infoRepository.findById(seq).orElseThrow(FileNotFoundException::new);
        return getFilePath(item);
    }

    public String getFileUrl(FileInfo item) {
            Long seq = item.getSeq();
            String extension = Objects.requireNonNullElse(item.getExtension(), "");
            return String.format("%S%s%s/%s", request.getContextPath(), properties.getUrl(), getFolder(seq), seq + extension);
    }

    public String getFileUrl(Long seq) {
        FileInfo item = infoRepository.findById(seq).orElseThrow(FileNotFoundException::new);
        return getFileUrl(item);
    }

    private long getFolder(long seq) {
        return seq % 10L;

    }

}
