package io.github.devup.tripfinder.board.service;

import io.github.devup.tripfinder.auth.entity.Users;
import io.github.devup.tripfinder.board.dto.request.BoardCreateRequest;
import io.github.devup.tripfinder.board.dto.request.BoardUpdateRequest;
import io.github.devup.tripfinder.board.dto.request.CommentCreatRequest;
import io.github.devup.tripfinder.board.entity.Board;
import io.github.devup.tripfinder.board.entity.BoardComment;
import io.github.devup.tripfinder.board.entity.BoardImg;
import io.github.devup.tripfinder.board.repository.BoardCommentRepository;
import io.github.devup.tripfinder.board.repository.BoardImgRepository;
import io.github.devup.tripfinder.board.repository.BoardRepository;
import io.github.devup.tripfinder.board.repository.LikeBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardImgRepository boardImgRepository;
    private final LikeBoardRepository likeBoardRepository;

    @Transactional
    public Board createdBoard(Users writer , BoardCreateRequest request){
        // 공지글이면 관리자인지 체크하는곳
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
    public void updateBoard(Long boardId, Users requester, BoardUpdateRequest request){
        Board board = findBoardById(boardId);
        checkWriterOrAdmin(board,requester);
        board.update(request.getTitle(),request.getContents());
    }

    @Transactional
    public void deleteBoard(Long boardId , Users requester){
        Board board = findBoardById(boardId);
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

    @Transactional
    public BoardComment createComment(Long boardId, Users writer, CommentCreatRequest request){
        Board board = findBoardById(boardId);

        BoardComment parent = null;
        if(request.getParentId() != null) {
            parent = boardCommentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("대댓글이 없습니다 . "));
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
    public void updateComment(Long commentId,Users requester,String contents){
        BoardComment comment = findCommentById(commentId);

        boolean isWriter = comment.getUser().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole().equals("ADMIN");
        if(!isWriter && !isAdmin){
            throw new AccessDeniedException("작성자 본인 또는 관리자만 가능합니다");
        }
        comment.updateContents(contents);
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
