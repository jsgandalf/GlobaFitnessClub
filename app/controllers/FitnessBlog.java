package controllers;

import models.Post;
import models.User;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Before;
import play.mvc.Controller;

import java.util.List;

public class FitnessBlog extends Controller{

    @Before
    static void addUser() {
        User user = connected();
        if(user != null) {
            renderArgs.put("user", user);
            session.put("isHome","true");
		}
    }

    static User connected() {
        if(renderArgs.get("user") != null) {
            return renderArgs.get("user", User.class);
        }
        String email = session.get("user");
        if(email != null) {
            return User.find("byEmail", email).first();
        }
        return null;
    }

	public static void index(){
		User user = connected();
		Post frontPost = Post.find(
    		"select p from Post p where p.author = ? order by postedAt desc",user).first();
		List<Post> olderPosts = Post.find(
    		"select p from Post p where p.author = ? order by postedAt desc",user).from(1).fetch(10);
        render(user, frontPost, olderPosts);
	}

	public static void showPost(Long id) {
        Post post = Post.findById(id);
        String randomID = Codec.UUID();
		User user = connected();
		render(user, post, randomID);
    }
	public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText("#E4EAFD");
        Cache.set(id, code, "30mn");
        renderBinary(captcha);
    }
	public static void listTagged(String tag) {
        List<Post> posts = Post.findTaggedWith(tag);
        render(tag, posts);
    }

	public static void postComment(
			Long postId,
			@Required(message="Author is required") String author,
			@Required(message="A message is required") String content,
			@Required(message="Please type the code") String code,
			String randomID)
	{
		Post post = Post.findById(postId);
		validation.equals(code, Cache.get(randomID)).message("Invalid code. Please type it again");
		if(validation.hasErrors()) {
			render("@showPost", post, randomID);
		}
		post.addComment(author, content);
		flash.success("Thanks for posting %s", author);
		Cache.delete(randomID);
		showPost(postId);
	}

	public static void createPost(){
		User user = connected();
		render(user);
	}

	public static void createPostFunction(@Required(message="Title is required") String post_title, @Required(message="Content is required") String post_content)
	{
		if(validation.hasErrors()) {
			render("@createPost", post_title, post_content);
		}
		Post post = new Post(connected(),post_title,post_content);
		post.create();
		index();
	}
}