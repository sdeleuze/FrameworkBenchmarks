package hello.repository;

import java.util.List;

import hello.model.Fortune;
import hello.model.World;

public interface DbRepository {

	World getWorld(int id);

	List<World> updateWorlds(int[] ids);

	List<Fortune> fortunes();
}
