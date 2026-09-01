package io.github.devup.tripfinder.board.service;

import io.github.devup.tripfinder.auth.entity.Users;
import io.github.devup.tripfinder.auth.repository.UsersRepository;
import io.github.devup.tripfinder.board.dto.request.BoardCreateRequest;
import io.github.devup.tripfinder.board.dto.request.BoardUpdateRequest;
import io.github.devup.tripfinder.board.dto.request.CommentCreateRequest;
import io.github.devup.tripfinder.board.entity.Board;
import io.github.devup.tripfinder.board.entity.BoardComment;
import io.github.devup.tripfinder.board.entity.BoardImg;
import io.github.devup.tripfinder.board.entity.LikeBoard;
import io.github.devup.tripfinder.board.repository.BoardCommentRepository;
import io.github.devup.tripfinder.board.repository.BoardImgRepository;
import io.github.devup.tripfinder.board.repository.BoardRepository;
import io.github.devup.tripfinder.board.repository.LikeBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardImgRepository boardImgRepository;
    private final LikeBoardRepository likeBoardRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public Board createBoard(Long writerId , BoardCreateRequest request){
        // 공지글이면 관리자인지 체크하는곳
        Users writer = findUserById(writerId);
        if (request.getCategory().equals("NOTICE") && !writer.getRole().equals("ADMIN")) {
            throw new AccessDeniedException("공지글은 관리자만 작성할 수 있습니다.");
        }
        Board board = Board.builder()
                .user(writer)
                .category(request.getCategory())
                .title(request.getTitle())
                .contents(request.getContents())
                .build();
        boardRepository.save(board);

        if(request.getImgUrls() != null){
            for(int i = 0 ; i< request.getImgUrls().size(); i++){
                boardImgRepository.save(BoardImg.builder()
                        .board(board)
                        .imgUrl(request.getImgUrls().get(i))
                        .imgOrder(i+1)
                        .build()
                );
            }
        }
        return board;
    }

    // findById로 가져온 객체의 값을 내가 바꾸면
    // @Transactional 범위가 끝날 때 JPA가 변경을 감지해서 자동으로 UPDATE 쿼리를 실행한다
    @Transactional
    public void updateBoard(Long boardId, Long requesterId, BoardUpdateRequest request){
        Board board = findBoardById(boardId);
        Users requester = findUserById(requesterId);
        checkWriterOrAdmin(board,requester);
        board.update(request.getTitle(),request.getContents());
    }

    @Transactional
    public void deleteBoard(Long boardId , Long requesterId){
        Board board = findBoardById(boardId);
        Users requester = findUserById(requesterId);
        checkWriterOrAdmin(board,requester);
        boardImgRepository.deleteAllByBoard(board);
        boardCommentRepository.deleteAllByBoard(board);
        likeBoardRepository.deleteAllByBoard(board);
        boardRepository.delete(board);
    }

    @Transactional
    public Board getBoard(Long boardId){
        Board board = findBoardById(boardId);
        board.increaseHit(); // findbyid 이걸로 찾은값을 변경시키면 어노테이션이 update해줌
        return board;
    }


    @Transactional //단순 조회
    public List<BoardComment> getComment(Long boardId){
        Board board = findBoardById(boardId);
        return boardCommentRepository.findByBoard(board);
    }

    @Transactional(readOnly = true)
    public long getLikeCount(Long boardId){
        Board board = findBoardById(boardId);
        return likeBoardRepository.countByBoard(board);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long boardId, Long requesterId){
        Board board = findBoardById(boardId);
        Users requester = findUserById(requesterId);
        return likeBoardRepository.existsByUserAndBoard(requester,board);
    }

    @Transactional
    public BoardComment createComment(Long boardId, Long writerId, CommentCreateRequest request){
        Board board = findBoardById(boardId);
        Users writer = findUserById(writerId);

        BoardComment parent = null;
        if(request.getParentId() != null) {
            parent = boardCommentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 없습니다"));
        }
        BoardComment comment = BoardComment.builder()
                .board(board)
                .user(writer)
                .parent(parent)
                .contents(request.getContents())
                .build();
        return boardCommentRepository.save(comment);
    }


    @Transactional
    public void updateComment(Long commentId,Long requesterId,String contents){
        BoardComment comment = findCommentById(commentId);
        Users requester = findUserById(requesterId);
        boolean isWriter = comment.getUser().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole().equals("ADMIN");
        if(!isWriter && !isAdmin){
            throw new AccessDeniedException("작성자 본인 또는 관리자만 가능합니다");
        }
        comment.updateContents(contents);
    }


    @Transactional(readOnly = true) // 단순 조회
    public List<Board> getBoardList(String category){
        if(category == null){
            return boardRepository.findAllByOrderByCreatedAtDesc();
        }
        return boardRepository.findAllByCategory(category); //NOTICE(공지) , REVIEW(리뷰나누기 일반게시판)
    }

    @Transactional
    public void toggleLike(Long boardId, Long requesterId){
        Board board = findBoardById(boardId);
        Users requester = findUserById(requesterId);
        if(likeBoardRepository.existsByUserAndBoard(requester, board)){
            likeBoardRepository.deleteByUserAndBoard(requester,board); //이미눌렀으면 취소하는기능
        }else{
            likeBoardRepository.save(LikeBoard.builder().user(requester).board(board).build()); //새로 저장
        }
    }

    @Transactional
    public void deleteComment(Long commentId , Long requesterId){
        BoardComment comment = findCommentById(commentId);
        Users requester = findUserById(requesterId);
        boolean isWriter = comment.getUser().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole().equals("ADMIN");
        if(!isWriter && !isAdmin){
            throw new AccessDeniedException("작성자 본인 또는 관리자만 가능합니다.");
        }
        boardCommentRepository.delete(comment);
    }

    @Transactional(readOnly = true) //다른메서드들이랑 스타일맞추기
    public List<String> uploadImages(List<MultipartFile> files) throws IOException {
        List<String> urls = new ArrayList<>();
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "board" + File.separator;

        File dir = new File(uploadDir);
        if(!dir.exists()) dir.mkdirs();

        for(MultipartFile file : files){
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();  //원래있는 파일이랑 안겹치게
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest); //실제로 업로드된 파일 내용을 그 경로에 저장 (진짜 디스크에 쓰는 부분)
            urls.add("/uploads/board/" + fileName); // 저장 성공한 파일의 접근 URL을 리스트에 추가
            // 이 URL이 나중에 게시글 작성 시 BoardCreateRequest.imgUrls에 담겨서 쓰임
        }
        return urls;
    }

    //이아래쪽은 중복부분 줄여둔거
    private Users findUserById(Long userId){
        return usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저없음"));
    }

    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
    }

    private void checkWriterOrAdmin(Board board,Users requester){
        boolean isWriter = board.getUser().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole().equals("ADMIN");
        if(!isWriter && !isAdmin){
            throw new AccessDeniedException("작성자 본인 또는 관리자만 가능합니다");
        }
    }

    private BoardComment findCommentById(Long commentId) {
        return boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));
    }

}
