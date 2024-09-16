package com.example.scrap.scrap;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.data.PolicyData;
import com.example.scrap.base.enums.QueryRange;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.entity.TrashScrap;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.web.category.ICategoryQueryService;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.scrap.IScrapQueryService;
import com.example.scrap.web.scrap.ScrapCommandServiceImpl;
import com.example.scrap.web.scrap.ScrapRepository;
import com.example.scrap.web.scrap.TrashScrapRepository;
import com.example.scrap.web.scrap.dto.ScrapRequest.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScrapCommandServiceImplTest {

    @InjectMocks
    private ScrapCommandServiceImpl scrapCommandService;

    @Mock
    private IMemberQueryService memberQueryService;

    @Mock
    private ICategoryQueryService categoryQueryService;

    @Mock
    private ScrapRepository scrapRepository;

    @Mock
    private IScrapQueryService scrapQueryService;

    @Mock
    private TrashScrapRepository trashScrapRepository;

    private final String scrapURL = "https://scrap";
    private final String imageURL = "https://image";
    private final String title = "스크랩 제목";
    private final boolean isFavorite = false;
    private final String memo= "메모메모";
    private final String description = "본문 내용";


    @DisplayName("스크랩 생성하기")
    @Test
    public void createScrap(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);
        ReflectionTestUtils.setField(category, "id", 99L);

        // 생성할 스크랩 설정
        CreateScrapDTO requestDTO = new CreateScrapDTO(scrapURL, imageURL, title, description, memo, isFavorite);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryQueryService.findCategory(category.getId(), member)).thenReturn(category);
        when(scrapRepository.countAllByMember(member)).thenReturn(PolicyData.SCRAP_CREATE_LIMIT-1);

        //** when
        Scrap newScrap = scrapCommandService.createScrap(memberDTO, category.getId(), requestDTO);

        //** then
        assertThat(newScrap.getScrapURL())
                .isEqualTo(requestDTO.getScrapURL());
        assertThat(newScrap.getImageURL())
                .isEqualTo(requestDTO.getImageURL());
        assertThat(newScrap.getDescription())
                .isEqualTo(requestDTO.getDescription());
        assertThat(newScrap.getIsFavorite())
                .isEqualTo(requestDTO.getIsFavorite());
        assertThat(newScrap.getMemo())
                .isEqualTo(requestDTO.getMemo());
        assertThat(newScrap.getTitle())
                .isEqualTo(requestDTO.getTitle());
    }

    // 카테고리랑 멤버 불일치할 때
    @DisplayName("[에러] 스크랩 생성 / 카테고리와 멤버 불일치")
    @Test
    public void createScrapError_categoryNotMatchToMember(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Member otherMember = Member.builder().build();
        Category category = setupCategory(otherMember, false);
        ReflectionTestUtils.setField(category, "id", 99L);

        // 생성할 스크랩 설정
        CreateScrapDTO requestDTO = new CreateScrapDTO(scrapURL, imageURL, title, description, memo, isFavorite);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryQueryService.findCategory(category.getId(), member)).thenThrow(
                new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH)
        );

        //** when
        Throwable throwable = catchThrowable(() -> {
            scrapCommandService.createScrap(memberDTO, category.getId(), requestDTO);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.CATEGORY_MEMBER_NOT_MATCH.getMessage());
    }

    @DisplayName("[에러] 스크랩 생성 / 스크랩 생성 개수 제한을 초과하였을 경우")
    @Test
    public void createScrapError_exceedScrapCreateLimit(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);
        ReflectionTestUtils.setField(category, "id", 99L);

        // 생성할 스크랩 설정
        CreateScrapDTO requestDTO = new CreateScrapDTO(scrapURL, imageURL, title, description, memo, isFavorite);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryQueryService.findCategory(category.getId(), member)).thenReturn(category);
        when(scrapRepository.countAllByMember(member)).thenReturn(PolicyData.SCRAP_CREATE_LIMIT);

        //** when
        Throwable throwable = catchThrowable(() -> {
            scrapCommandService.createScrap(memberDTO, category.getId(), requestDTO);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.EXCEED_SCRAP_CREATE_LIMIT.getMessage());
    }

    @DisplayName("스크랩 즐겨찾기 (단건) 즐겨찾기 X -> O")
    @Test
    public void toggleScrapFavorite_XtoO(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);

        // 스크랩 설정
        Scrap scrap = setupScrap(member, category, false);
        ReflectionTestUtils.setField(scrap, "id", 999L);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapQueryService.findScrap(scrap.getId())).thenReturn(scrap);

        //** when
        scrapCommandService.toggleScrapFavorite(memberDTO, scrap.getId());

        //** then
        assertThat(scrap.getIsFavorite())
                .isTrue();
    }

    @DisplayName("스크랩 즐겨찾기 (단건) 즐겨찾기 O -> X")
    @Test
    public void toggleScrapFavorite_OtoX(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);

        // 스크랩 설정
        Scrap scrap = setupScrap(member, category, true);
        ReflectionTestUtils.setField(scrap, "id", 999L);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapQueryService.findScrap(scrap.getId())).thenReturn(scrap);

        //** when
        scrapCommandService.toggleScrapFavorite(memberDTO, scrap.getId());

        //** then
        assertThat(scrap.getIsFavorite())
                .isFalse();
    }

    @DisplayName("[에러] 스크랩 즐겨찾기 (단건) / 스크랩과 멤버 불일치")
    @Test
    public void toggleScrapFavoriteError_scrapNotMatchToMember(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);

        // 스크랩 설정
        Member otherMember = Member.builder().build();
        Scrap scrap = setupScrap(otherMember, category, true);
        ReflectionTestUtils.setField(scrap, "id", 999L);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapQueryService.findScrap(scrap.getId())).thenReturn(scrap);

        //** when
        Throwable throwable = catchThrowable(() -> {
            scrapCommandService.toggleScrapFavorite(memberDTO, scrap.getId());
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.SCRAP_MEMBER_NOT_MATCH.getMessage());
    }

    @DisplayName("스크랩 즐겨찾기 (목록) / O(모두 즐겨찾기 됨)->X ")
    @Test
    public void toggleScrapFavoriteList_OtoX(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);
        ReflectionTestUtils.setField(category, "id", 99L);

        // 스크랩 설정
        List<Scrap> scrapList = new ArrayList<>();
        List<Long> scrapIdList = new ArrayList<>();
        for(int i=0; i<5; i++){
            Scrap scrap = setupScrap(member, category, true);
            ReflectionTestUtils.setField(scrap, "id", 999L - i);

            scrapList.add(scrap);
            scrapIdList.add(scrap.getId());
        }

        // requestDTO 설정
        ToggleScrapFavoriteListDTO requestDTO = new ToggleScrapFavoriteListDTO(scrapIdList);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapRepository.findAll(isA(Specification.class))).thenReturn(scrapList);

        //** when
        scrapCommandService.toggleScrapFavoriteList(memberDTO, requestDTO);

        //** then
        for(Scrap scrap : scrapList){
            assertThat(scrap.getIsFavorite())
                    .isFalse();
        }
    }

    @DisplayName("스크랩 즐겨찾기 (목록) - X(모두 즐겨찾기 안됨)->O ")
    @Test
    public void toggleScrapFavoriteList_XtoO(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);
        ReflectionTestUtils.setField(category, "id", 99L);

        // 스크랩 설정
        List<Scrap> scrapList = new ArrayList<>();
        List<Long> scrapIdList = new ArrayList<>();
        for(int i=0; i<5; i++){
            Scrap scrap = setupScrap(member, category, false);
            ReflectionTestUtils.setField(scrap, "id", 999L - i);

            scrapList.add(scrap);
            scrapIdList.add(scrap.getId());
        }

        // requestDTO 설정
        ToggleScrapFavoriteListDTO requestDTO = new ToggleScrapFavoriteListDTO(scrapIdList);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapRepository.findAll(isA(Specification.class))).thenReturn(scrapList);

        //** when
        scrapCommandService.toggleScrapFavoriteList(memberDTO, requestDTO);

        //** then
        for(Scrap scrap : scrapList){
            assertThat(scrap.getIsFavorite())
                    .isTrue();
        }
    }

    @DisplayName("스크랩 즐겨찾기 (목록) - 믹스->O ")
    @Test
    public void toggleScrapFavoriteList_MixToO(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);
        ReflectionTestUtils.setField(category, "id", 99L);

        // 스크랩 설정
        List<Scrap> scrapList = new ArrayList<>();
        List<Long> scrapIdList = new ArrayList<>();
        for(int i=0; i<5; i++){
            Scrap scrap = setupScrap(member, category, i%2 == 0);
            ReflectionTestUtils.setField(scrap, "id", 999L - i);

            scrapList.add(scrap);
            scrapIdList.add(scrap.getId());
        }

        // requestDTO 설정
        ToggleScrapFavoriteListDTO requestDTO = new ToggleScrapFavoriteListDTO(scrapIdList);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapRepository.findAll(isA(Specification.class))).thenReturn(scrapList);

        //** when
        scrapCommandService.toggleScrapFavoriteList(memberDTO, requestDTO);

        //** then
        for(Scrap scrap : scrapList){
            assertThat(scrap.getIsFavorite())
                    .isTrue();
        }
    }

    @DisplayName("스크랩 이동하기 (단건)")
    @Test
    public void moveCategoryOfScrap(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);
        Category moveCategory = setupCategory(member, false);
        ReflectionTestUtils.setField(moveCategory, "id", 98L);

        // 스크랩 설정
        Scrap scrap = setupScrap(member, category, true);
        ReflectionTestUtils.setField(scrap, "id", 999L);

        // requestDTO 설정
        MoveCategoryOfScrapDTO requestDTO = new MoveCategoryOfScrapDTO(moveCategory.getId());

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapQueryService.findScrap(scrap.getId())).thenReturn(scrap);
        when(categoryQueryService.findCategory(moveCategory.getId(), member)).thenReturn(moveCategory);

        //** when
        scrapCommandService.moveCategoryOfScrap(memberDTO, scrap.getId(), requestDTO);

        //** then
        assertThat(scrap.getCategory())
                .isEqualTo(moveCategory);
    }

    @DisplayName("[에러] 스크랩 이동하기 (단건) / 스크랩과 멤버가 일치하지 않음")
    @Test
    public void errorMoveCategoryOfScrapError_scrapNotMatchToMember(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);
        Category moveCategory = setupCategory(member, false);
        ReflectionTestUtils.setField(moveCategory, "id", 98L);

        // 스크랩 설정
        Member otherMember = Member.builder().build();
        Scrap scrap = setupScrap(otherMember, category, true);
        ReflectionTestUtils.setField(scrap, "id", 999L);

        // requestDTO 설정
        MoveCategoryOfScrapDTO requestDTO = new MoveCategoryOfScrapDTO(moveCategory.getId());

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapQueryService.findScrap(scrap.getId())).thenReturn(scrap);
        when(categoryQueryService.findCategory(moveCategory.getId(), member)).thenReturn(moveCategory);

        //** when
        Throwable throwable = catchThrowable(() -> {
            scrapCommandService.moveCategoryOfScrap(memberDTO, scrap.getId(), requestDTO);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.SCRAP_MEMBER_NOT_MATCH.getMessage());
    }

    @DisplayName("[에러] 스크랩 이동하기 (단건) / 카테고리와 멤버가 일치하지 않음")
    @Test
    public void errorMoveCategoryOfScrapError_categoryNotMatchToMember(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Member otherMember = Member.builder().build();
        Category category = setupCategory(member, false);
        Category moveCategory = setupCategory(otherMember, false);
        ReflectionTestUtils.setField(moveCategory, "id", 98L);

        // 스크랩 설정
        Scrap scrap = setupScrap(member, category, true);
        ReflectionTestUtils.setField(scrap, "id", 999L);

        // requestDTO 설정
        MoveCategoryOfScrapDTO requestDTO = new MoveCategoryOfScrapDTO(moveCategory.getId());

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapQueryService.findScrap(scrap.getId())).thenReturn(scrap);
        when(categoryQueryService.findCategory(moveCategory.getId(), member)).thenThrow(
                new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH)
        );

        //** when
        Throwable throwable = catchThrowable(() -> {
            scrapCommandService.moveCategoryOfScrap(memberDTO, scrap.getId(), requestDTO);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.CATEGORY_MEMBER_NOT_MATCH.getMessage());
    }

    @DisplayName("스크랩 이동하기 (목록)")
    @Test
    public void moveCategoryOfScraps() {
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);
        ReflectionTestUtils.setField(category, "id", 99L);
        Category moveCategory = setupCategory(member, false);
        ReflectionTestUtils.setField(moveCategory, "id", 98L);

        // 스크랩 설정
        List<Scrap> scrapList = setupScrapList(member, category, 10);

        // requestDTO 설정
        MoveCategoryOfScrapsDTO requestDTO = new MoveCategoryOfScrapsDTO(null, moveCategory.getId());

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryQueryService.findCategory(moveCategory.getId(), member)).thenReturn(moveCategory);
        when(scrapRepository.findAll(isA(Specification.class))).thenReturn(scrapList);

        //** when
        scrapCommandService.moveCategoryOfScraps(memberDTO, requestDTO);

        //** then
        for(Scrap scrap : scrapList){
            assertThat(scrap.getCategory())
                    .isEqualTo(moveCategory);
        }
    }

    @DisplayName("스크랩 메모 수정하기")
    @Test
    public void updateScrapMemo(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);

        // 스크랩 설정
        Scrap scrap = setupScrap(member, category, false);
        ReflectionTestUtils.setField(scrap, "id", 999L);

        // requestDTO 설정
        UpdateScrapMemoDTO requestDTO = new UpdateScrapMemoDTO(memo + " 수정");

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapQueryService.findScrap(scrap.getId())).thenReturn(scrap);

        //** when
        scrapCommandService.updateScrapMemo(memberDTO, scrap.getId(), requestDTO);

        //** then
        assertThat(scrap.getMemo())
                .isEqualTo(requestDTO.getMemo());
    }

    @DisplayName("[에러] 스크랩 메모 수정하기 / 스크랩과 멤버가 일치하지 않음")
    @Test
    public void updateScrapMemoError_scrapNotMatchToMember(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);

        // 스크랩 설정
        Member otherMember = Member.builder().build();
        Scrap scrap = setupScrap(otherMember, category, false);
        ReflectionTestUtils.setField(scrap, "id", 999L);

        // requestDTO 설정
        UpdateScrapMemoDTO requestDTO = new UpdateScrapMemoDTO(memo + " 수정");

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapQueryService.findScrap(scrap.getId())).thenReturn(scrap);

        //** when
        Throwable throwable = catchThrowable(() -> {
            scrapCommandService.updateScrapMemo(memberDTO, scrap.getId(), requestDTO);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.SCRAP_MEMBER_NOT_MATCH.getMessage());
    }

    @DisplayName("스크랩 삭제 (단건)")
    @Test
    public void throwScrapInTrash(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);

        // 스크랩 설정
        Scrap scrap = setupScrap(member, category, false);
        ReflectionTestUtils.setField(scrap, "id", 999L);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapQueryService.findScrap(scrap.getId())).thenReturn(scrap);

        //** when
        TrashScrap trashScrap = scrapCommandService.throwScrapIntoTrash(memberDTO, scrap.getId());

        //** then
        verify(scrapRepository).delete(scrap); // delete가 호출되었는지 확인
        verify(trashScrapRepository, times(1)).save(trashScrap);

        assertThat(trashScrap.getScrapURL())
                .isEqualTo(scrap.getScrapURL());
        assertThat(trashScrap.getImageURL())
                .isEqualTo(scrap.getImageURL());
        assertThat(trashScrap.getDescription())
                .isEqualTo(scrap.getDescription());
        assertThat(trashScrap.getIsFavorite())
                .isEqualTo(scrap.getIsFavorite());
        assertThat(trashScrap.getMemo())
                .isEqualTo(scrap.getMemo());
        assertThat(trashScrap.getTitle())
                .isEqualTo(scrap.getTitle());
    }

    @DisplayName("[에러] 스크랩 삭제 (단건) / 스크랩과 멤버가 일치하지 않음")
    @Test
    public void errorThrowScrapInTrash_scrapNotMatchToMember(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);

        // 스크랩 설정
        Member otherMember = Member.builder().build();
        Scrap scrap = setupScrap(otherMember, category, false);
        ReflectionTestUtils.setField(scrap, "id", 999L);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapQueryService.findScrap(scrap.getId())).thenReturn(scrap);

        //** when
        Throwable throwable = catchThrowable(() -> {
            scrapCommandService.throwScrapIntoTrash(memberDTO, scrap.getId());
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.SCRAP_MEMBER_NOT_MATCH.getMessage());
    }

    @DisplayName("스크랩 휴지통에 버리기 (목록)")
    @Test
    public void throwScrapListInTrash() {
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);
        ReflectionTestUtils.setField(category, "id", 99L);

        // 스크랩 설정
        List<Scrap> scrapList = new ArrayList<>();
        List<Long> scrapIdList = new ArrayList<>();
        for(int i=0; i<10; i++){
            Scrap scrap = setupScrap(member, category, true);

            scrapList.add(scrap);
            scrapIdList.add(scrap.getId());
        }

        // RequestDTO 설정
        DeleteScrapListDTO requestDTO = new DeleteScrapListDTO(scrapIdList);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(scrapRepository.findAll(isA(Specification.class))).thenReturn(scrapList);

        //** when
        List<TrashScrap> trashScrapList = scrapCommandService.throwScrapListIntoTrash(memberDTO, requestDTO);

        //** then
        verify(scrapRepository, times(scrapList.size())).delete(isA(Scrap.class));
        verify(trashScrapRepository, times(scrapList.size())).save(isA(TrashScrap.class));

        assertThat(trashScrapList.size())
                .isEqualTo(scrapList.size());
    }

    @DisplayName("휴지통에 스크랩 버리기")
    @Test
    public void throwScrapIntoTrash(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member, false);
        ReflectionTestUtils.setField(category, "id", 99L);

        // 스크랩 설정
        Scrap scrap = setupScrap(member, category, false);

        //** when
        TrashScrap trashScrap = scrapCommandService.throwScrapIntoTrash(scrap);

        //** then
        verify(scrapRepository).delete(isA(Scrap.class));
        verify(trashScrapRepository).save(isA(TrashScrap.class));

        assertThat(trashScrap.getScrapURL())
                .isEqualTo(scrap.getScrapURL());
        assertThat(trashScrap.getImageURL())
                .isEqualTo(scrap.getImageURL());
        assertThat(trashScrap.getDescription())
                .isEqualTo(scrap.getDescription());
        assertThat(trashScrap.getIsFavorite())
                .isEqualTo(scrap.getIsFavorite());
        assertThat(trashScrap.getMemo())
                .isEqualTo(scrap.getMemo());
        assertThat(trashScrap.getTitle())
                .isEqualTo(scrap.getTitle());
    }

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
                .title(title)
                .scrapURL(scrapURL)
                .imageURL(imageURL)
                .memo(memo)
                .description(description)
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
