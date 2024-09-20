package hello.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import hello.Utils;
import hello.model.Fortune;
import hello.model.World;

@Repository
@Profile("mongo")
public class MongoDbRepository implements DbRepository {
	private final MongoTemplate mongoTemplate;

	public MongoDbRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public World getWorld(int id) {
		return mongoTemplate.findById(id, World.class);
	}

	@Override
	// TODO Check if we can optimize with mongoTemplate#updateMulti while still complying to the test requirements
	public List<World> updateWorlds(int[] ids) {
		List<World> worlds = new ArrayList<>(ids.length);
		String collectionName = mongoTemplate.getCollectionName(World.class);
		for (int id : ids) {
			World world = getWorld(id);
			world.randomNumber = Utils.randomWorldNumber();
			worlds.add(mongoTemplate.save(world, collectionName));
		}
		return worlds;
	}

	@Override
	public List<Fortune> fortunes() {
		return mongoTemplate.findAll(Fortune.class);
	}
}
