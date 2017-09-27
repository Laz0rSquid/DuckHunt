package DuckHunt;

/**
 *
 * @author Marcus Kopp <marcus.kopp86 at gmail.com>
 */
public enum Sound {
	GUNFIRE("res/gun_fire.wav"),
	DUCK("res/duck_hit.wav"),
	DEVIL("res/devil_hit.wav"),
	ENDGAME("res/endgame_sound.wav");
	
	private String pathToSoundFile;
	
	private Sound(String path) {
		this.pathToSoundFile = path;
	}

	public String getPath() {
		return pathToSoundFile;
	}
	
	
}
