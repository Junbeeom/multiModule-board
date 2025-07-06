package kube.board.article.service.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PageLimitCalculatorTest {
    
    @Test
    @DisplayName("페이징 테스트")
    void calculatePageLimitTest() {
        calculatePageLimitTest2(1L, 30L, 10L, 301L );
    }

    void calculatePageLimitTest2(Long page, Long pageSize, Long movablePageCount, Long expected) {
        Long result = PageLimitCalculator.calculatePageLimit(page, pageSize, movablePageCount);
         assertThat(result).isEqualTo(expected);
    }

}