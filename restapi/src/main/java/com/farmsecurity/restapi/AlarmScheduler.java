package com.farmsecurity.restapi;

import com.farmsecurity.restapi.model.Log;
import com.farmsecurity.restapi.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@EnableScheduling
@Configuration
@RequiredArgsConstructor
public class AlarmScheduler {

    private LogRepository logRepository;

    /**
     * 매일 날짜 체크
     * 매일 0시 1분에 날짜를 체크하도록 작동합니다.
     * 해당 시간에 되면 스프링 Scheduled 에 등록되어 있는 DailyDateCompare 가 작동해서
     * 전체 그룹 목록을 가져와서 그룹들의 openDate 와 현재 날짜를 비교하여
     * 알람을 전송합니다.
     */

    //매일 0시 1분에 날짜 체크
    @Scheduled(cron = "0 40 22 * * * ")
    public void DailyCheck(){
        int idx = 0;
        List<Log> logs = logRepository.findAll();

        LocalDateTime today = LocalDateTime.now();
        for (Log log : logs) {
            String time = logs.get(idx).getTime();
            CompareDate(time, today);
            idx ++;
        }
    }

    private void CompareDate (String t, LocalDateTime today){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.parse(t,formatter);
        LocalDate timeDate = LocalDate.from(time);
        LocalDate timeDate2 = timeDate.plusMonths(1);
        LocalDate todayDate = LocalDate.from(today);
        if(timeDate.isEqual(todayDate)){
            logRepository.deleteByTime(String.valueOf(time));
        }
    }
}