package com.example.scrap.scrap;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.web.category.ICategoryQueryService;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.scrap.ScrapQueryServiceImpl;
import com.example.scrap.web.scrap.ScrapRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScrapQueryServiceImplTest {

    @InjectMocks
    private ScrapQueryServiceImpl scrapQueryService;

    @Mock
    private IMemberQueryService memberQueryService;

    @Mock
    private ICategoryQueryService categoryQueryService;

    @Mock
    private ScrapRepository scrapRepository;

    @DisplayName("[에러] 카테고리별 스크랩 조회시, 기준이 되는 카테고리와 사용자가 일치하지 않음")
    @Test
    public void whenGetScrapListCategoryNotMatchToMember() {
        //** given
        // 멤버 설정
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Member otherMember = Member.builder().build();
        Category category = setupCategory(otherMember, false);
        ReflectionTestUtils.setField(category, "id", 99L);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryQueryService.findCategory(category.getId(), member)).thenThrow(
                new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH)
        );

        //** when
        Throwable throwable = catchThrowable(() -> {
            scrapQueryService.getScrapListByCategory(memberDTO, category.getId(), null);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.CATEGORY_MEMBER_NOT_MATCH.getMessage());
    }

    @DisplayName("스크랩 세부 조회")
    @Test
    public void getScrapDetail() {
        //** given
        // 멤버 설정
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);

        // 스크랩 설정
        Scrap scrap = setupScrap(member, category, false);
        ReflectionTestUtils.setField(scrap, "id", 999L);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapRepository.findById(scrap.getId())).thenReturn(Optional.of(scrap));

        //** when
        Scrap findScrap = scrapQueryService.getScrapDetails(memberDTO, scrap.getId());

        //** then
        assertThat(findScrap.getId())
                .isEqualTo(scrap.getId());
    }

    @DisplayName("[에러] 스크랩 세부 조회 시, 스크랩과 사용자가 일치하지 않음")
    @Test
    public void whenGetScrapDetailScrapNotMatchToMember(){
        //** given
        // 멤버 설정
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);

        // 스크랩 설정
        Member otherMember = Member.builder().build();
        Scrap scrap = setupScrap(otherMember, category, false);
        ReflectionTestUtils.setField(scrap, "id", 999L);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapRepository.findById(scrap.getId())).thenReturn(Optional.of(scrap));

        //** when
        Throwable throwable = catchThrowable(() -> {
            scrapQueryService.getScrapDetails(memberDTO, scrap.getId());
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.SCRAP_MEMBER_NOT_MATCH.getMessage());
    }

    @DisplayName("ID로 스크랩 조회")
    @Test
    public void findScrapById(){
        //** given
        // 멤버 설정
        Member member = setupMember();

        // 카테고리 설정
        Category category = setupCategory(member, false);

        // 스크랩 설정
        Scrap scrap = setupScrap(member, category, false);
        ReflectionTestUtils.setField(scrap, "id", 999L);

        when(scrapRepository.findById(scrap.getId())).thenReturn(Optional.of(scrap));

        //** when
        Scrap findScrap = scrapQueryService.findScrap(scrap.getId());

        //** then
        assertThat(findScrap.getId())
                .isEqualTo(scrap.getId());
        assertThat(findScrap.getCategory())
                .isEqualTo(scrap.getCategory());
        assertThat(findScrap.getMember())
                .isEqualTo(scrap.getMember());
    }

    // TODO: createSpecByQueryRange(), findAllByRequest()에 대한 테스트 코드 작성

    private Member setupMember(){
        return Member.builder()
                .name("홍길동")
                .snsId("testSnsId")
                .snsType(SnsType.NAVER)
                .build();
    }

    private MemberDTO setupMemberDTO(Member member){
        return MemberDTO.builder()
                .memberId(member.getId())
                .snsId(member.getSnsId())
                .snsType(member.getSnsType())
                .build();
    }

    /**
     * 카테고리 id 임의 설정 해줌
     */
    private List<Category> setupCategoryList(Member member, int size){
        List<Category> categoryList = new ArrayList<>();

        Category category;
        for(int i=0; i<size; i++){
            category = Category.builder()
                    .title("카테고리" + i)
                    .sequence(i+1)
                    .isDefault(i==0 ? true : false) // 하나는 기본 카테고리로 만들기
                    .member(member)
                    .build();

            ReflectionTestUtils.setField(category, "id", (long) (i + 1));

            categoryList.add(category);
        }

        return categoryList;
    }

    /**
     * 카테고리 id 설정 안해줌
     */
    private Category setupCategory(Member member, boolean isDefault){
        return Category.builder()
                .member(member)
                .title("카테고리명")
                .sequence(1)
                .isDefault(isDefault)
                .build();
    }

    private Scrap setupScrap(Member member, Category category, boolean isFavorite){
        return Scrap.builder()
                .member(member)
                .category(category)
                .title("스크랩 제목")
                .scrapURL("https://scrap")
                .imageURL("https://image")
                .memo("메모메모")
                .description("본문 내용")
                .isFavorite(isFavorite)
                .build();
    }

    private List<Scrap> setupScrapList(Member member, Category category, int size){
        List<Scrap> scrapList = new ArrayList<>();

        Scrap scrap;
        for(int i=0; i<size; i++){
            scrap = setupScrap(member, category, i%2 == 0);

            scrapList.add(scrap);
        }

        return scrapList;
    }
}
