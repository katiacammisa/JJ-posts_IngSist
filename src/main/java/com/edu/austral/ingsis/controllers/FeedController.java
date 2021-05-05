package com.edu.austral.ingsis.controllers;

import com.edu.austral.ingsis.dtos.post.PostDTO;
import com.edu.austral.ingsis.entities.Post;
import com.edu.austral.ingsis.services.PostService;
import com.edu.austral.ingsis.services.ThreadService;
import com.edu.austral.ingsis.utils.ObjectMapper;
import com.edu.austral.ingsis.utils.ObjectMapperImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.edu.austral.ingsis.utils.ConnectMicroservices.connectToUserMicroservice;
import static com.edu.austral.ingsis.utils.ConnectMicroservices.setUserDetails;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.PUT})
public class FeedController {

  private final ObjectMapper objectMapper;
  private final PostService postService;
  private final ThreadService threadService;

  public FeedController(PostService postService, ThreadService threadService) {
    this.postService = postService;
    this.threadService = threadService;
    this.objectMapper = new ObjectMapperImpl();
  }

  @GetMapping("/posts/most-liked")
  public ResponseEntity<List<PostDTO>> getMostLikedPosts(@RequestParam(name = "size", defaultValue = "10") int size) {
    final List<Post> posts = postService.getMostLiked(size);
    List<PostDTO> updatedPostDTOS = new ArrayList<>();
    for(PostDTO dto : objectMapper.map(posts, PostDTO.class)) {
      updatedPostDTOS.add(setThreadId(setUserDetails(connectToUserMicroservice("/user/" + dto.getUser(), HttpMethod.GET), dto)));
    }
    return ResponseEntity.ok(updatedPostDTOS);
  }

  private PostDTO setThreadId(PostDTO postDTO) {
    postDTO.setThreadId(threadService.getByPostId(postDTO.getId()).getId());
    return postDTO;
  }
}
