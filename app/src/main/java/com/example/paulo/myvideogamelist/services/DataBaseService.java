package com.example.paulo.myvideogamelist.services;

import com.example.paulo.myvideogamelist.models.Developer;
import com.example.paulo.myvideogamelist.models.Game;
import com.example.paulo.myvideogamelist.models.GameList;
import com.example.paulo.myvideogamelist.models.GameList_;
import com.example.paulo.myvideogamelist.models.Game_;
import com.example.paulo.myvideogamelist.models.ListGame;
import com.example.paulo.myvideogamelist.models.ListGame_;
import com.example.paulo.myvideogamelist.models.Review;
import com.example.paulo.myvideogamelist.models.Review_;
import com.example.paulo.myvideogamelist.models.User;
import com.example.paulo.myvideogamelist.models.User_;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

public class DataBaseService {

    AuthService authService;
    BoxStore boxStore;
    Box<GameList> gameListBox;
    Box<User> userBox;
    Box<ListGame> listGameBox;
    Box<Game> gameBox;
    Box<Review> reviewBox;
    Box<Developer> developerBox;


    public DataBaseService(AuthService authService, BoxStore boxStore) {
        this.authService = authService;
        this.boxStore = boxStore;
        this.gameListBox = boxStore.boxFor(GameList.class);
        this.userBox = boxStore.boxFor(User.class);
        this.listGameBox = boxStore.boxFor(ListGame.class);
        this.gameBox = boxStore.boxFor(Game.class);
        this.reviewBox = boxStore.boxFor(Review.class);
        this.developerBox = boxStore.boxFor(Developer.class);
    }

    public void populateDevelopers(){
        if(developerBox.getAll().size() < 2) {
            Developer developer1 = new Developer("test 1", "test 1");
            Developer developer2 = new Developer("square", "square enix ");
            Developer developer = new Developer("nintendo", "nintendo");

            developerBox.put(developer);
            developerBox.put(developer1);
            developerBox.put(developer2);
        }
    }

    public List<GameList> getAllListByUser(User user){
        return gameListBox.query().equal(GameList_.userId,user.id).build().find();
    }

    public  List<GameList> getAllCurrentUserLists (){
        return getAllListByUser(authService.getCurrentUser());
    }

    public List<Game> getAllGamesInAList(GameList gameList){
        List<ListGame> listGameList = listGameBox.query().equal(ListGame_.listId,gameList.getId()).build().find();
        long[] ids = new long[listGameList.size()];
        int i = 0;
        for (ListGame list: listGameList){
            ids[i] = list.getGame().id;
            i ++ ;
        }
        return  gameBox.query().in(Game_.id,ids ).build().find();
    }

    public void deleteGame (Game game){
        listGameBox.remove(listGameBox.query().equal(ListGame_.gameId,game.id).build().find());
        gameBox.remove(game);
    }

    public void removeGameFromList(Game game,GameList gameList){
        ListGame listGame = listGameBox.query().equal(ListGame_.gameId,game.id).equal(ListGame_.listId,gameList.getId()).build().findFirst();
        listGameBox.remove(listGame);

    }

    public void deleteReview (Review review){
        reviewBox.remove(review);
    }

    public void deleteGameList (GameList gameList){

        List<ListGame> listGamesRelation = listGameBox.query().equal(ListGame_.listId,gameList.getId()).build().find();
        listGameBox.remove(listGamesRelation);
        gameListBox.remove(gameList);

    }

    public List<Review> getAllReviewToGame(Game game){
        QueryBuilder builder = reviewBox.query().equal(Review_.targetId,game.id);
        return builder.build().find();

    }

    public List<Game> getAllDeveloperGames(Developer developer){
        return gameBox.query().equal(Game_.developerId,developer.id).build().find();
    }

    public List<Game> getAllGames (){
       return gameBox.query().build().find();
    }

    public List<Developer> getAllDevelopers () { return developerBox.query().build().find();}
}
