package hello.web;

import java.util.ArrayList;
import java.util.List;

import hello.Utils;
import hello.model.Fortune;
import hello.model.World;
import hello.repository.DbRepository;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.RenderingResponse;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import static java.util.Comparator.comparing;

@Component
public class DbHandler {

	private final DbRepository dbRepository;

	public DbHandler(DbRepository dbRepository) {
		this.dbRepository = dbRepository;
	}

	ServerResponse db(ServerRequest request) {
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(dbRepository.getWorld(Utils.randomWorldNumber()));
	}

	ServerResponse queries(ServerRequest request) {
		int queries = parseQueryCount(request.params().getFirst("queries"));
		int[] ids = Utils.randomWorldNumbers(queries);
		List<World> worlds = new ArrayList<>(queries);
		for (int id : ids) {
			worlds.add(dbRepository.getWorld(id));
		}
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(worlds);
	}

	ServerResponse updates(ServerRequest request) {
		int queries = parseQueryCount(request.params().getFirst("queries"));
		int[] randomWorldNumbers = Utils.randomWorldNumbers(queries);
		List<World> worlds = dbRepository.updateWorlds(randomWorldNumbers);
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(worlds);
	}

	ServerResponse fortunes(ServerRequest request) {
		var fortunes = dbRepository.fortunes();
		fortunes.add(new Fortune(0, "Additional fortune added at request time."));
		fortunes.sort(comparing(fortune -> fortune.message));
		return RenderingResponse
				.create("fortunes")
				.modelAttribute("fortunes", fortunes)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE)
				.build();
	}

	private static int parseQueryCount(String textValue) {
		if (textValue == null) {
			return 1;
		}
		int parsedValue;
		try {
			parsedValue = Integer.parseInt(textValue);
		} catch (NumberFormatException e) {
			return 1;
		}
		return Math.min(500, Math.max(1, parsedValue));
	}
}
