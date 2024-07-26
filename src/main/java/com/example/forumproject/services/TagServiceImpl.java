package com.example.forumproject.services;

import com.example.forumproject.exceptions.AuthorizationException;
import com.example.forumproject.exceptions.DuplicateEntityException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.exceptions.OperationAlreadyPerformedException;
import com.example.forumproject.helpers.AuthenticationHelper;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.Tag;
import com.example.forumproject.models.User;
import com.example.forumproject.models.dtos.out.TagAdminDto;
import com.example.forumproject.models.dtos.out.TagUserDto;
import com.example.forumproject.repositories.contracts.PostRepository;
import com.example.forumproject.repositories.contracts.TagRepository;
import com.example.forumproject.services.contracts.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {
    private static final String TAG_ERROR_MESSAGE = "Operation can be performed only by admin";
    public static final String ERROR_MESSAGE = "You are not authorized to browse user information.";
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    @Autowired
    public TagServiceImpl(TagRepository tagRepository, PostRepository postRepository){
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }
    @Override
    public List<?> getAllTags(User user) {
        List<Tag> tags = tagRepository.findAll();
        if (user.getRole().getRoleName().equals("Admin")){
            return tags.stream().map(tag -> new TagAdminDto(tag.getId(), tag.getTagName())).collect(Collectors.toList());
        } else {
            return tags.stream().map(tag -> new TagUserDto(tag.getTagName())).collect(Collectors.toList());
        }
    }

    @Override
    public Optional<Tag> getById(User user, int tagId) {
        checkPermissions(user);
        return tagRepository.findById(tagId);
    }
    //TODO make method transactional... move to Post controller
    @Override
    public void addTagToPost(int id, int postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new EntityNotFoundException("Post", postId));
        checkAccessPermissions(post, user);
        Tag tag = tagRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Tag", id));
        checkForAlreadyDoneOperation(post, tag, true);
        post.getTags().add(tag);
        tag.getPosts().add(post);
        tagRepository.save(tag);
        postRepository.save(post);
    }
    //TODO make method transactional... move to Post controller
    @Override
    public void removeTagToPost(int id, int postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new EntityNotFoundException("Post", postId));
        checkAccessPermissions(post, user);
        Tag tag = tagRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Tag", id));
        checkForAlreadyDoneOperation(post, tag, false);
        post.getTags().remove(tag);
        tag.getPosts().remove(post);
        tagRepository.save(tag);
        postRepository.save(post);
    }

    @Override
    public Tag update(Tag tag, User user) {
        checkPermissions(user);
        return tagRepository.save(tag);
    }

    @Override
    public Tag create(Tag tag, User user) {
        try {
            checkPermissions(user);
            return tagRepository.save(tag);
        }catch (DataIntegrityViolationException e) {
            throw new DuplicateEntityException("Tag with this name already exists.");
        }
    }

    @Override
    public void delete(int id, User user) {
        checkPermissions(user);
        Tag tagToDelete = tagRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Tag", id));
        tagRepository.delete(tagToDelete);
    }

    private void checkPermissions(User user) {
        String roleName = user.getRole().getRoleName();
        if (roleName.equals("Admin")){
            return;
        }
        throw new AuthorizationException(TAG_ERROR_MESSAGE);
    }
    public static void checkAccessPermissions(Post post, User executingUser) {
        if (!post.getCreatedBy().equals(executingUser)) {
            throw new AuthorizationException(ERROR_MESSAGE);
        }
    }
    public static void checkForAlreadyDoneOperation(Post post, Tag tag, boolean isAddOperation) {
        boolean alreadyDone = isAddOperation ? post.getTags().contains(tag) : !post.getTags().contains(tag);
        if (alreadyDone) {
            String operation = isAddOperation ? "add" : "remove";
            throw new OperationAlreadyPerformedException("Cannot " + operation + " tag. Operation already performed.");
        }
    }
}
