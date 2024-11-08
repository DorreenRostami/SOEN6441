package modules;

import com.google.inject.AbstractModule;
import services.YouTubeServiceInterface;
import services.YouTubeService;

public class MyModule extends AbstractModule {
    @Override
    protected void configure() {
        // bind the YouTubeService interface to the YouTubeService class
        bind(YouTubeServiceInterface.class).to(YouTubeService.class);
    }
}