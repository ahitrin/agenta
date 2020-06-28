import agenta.*;

class AgentaTest {
	public static void main(String[] args) {
		if(args.length != 3){
			System.err.println("Format: AgentaTest placement_file ai1_file ai2_file");
			System.exit(0);
		}
		CommandLineInitiator cli = new CommandLineInitiator(args[0],
				new SimpleCommander(args[1]), new SimpleCommander(args[2]));    
		Engine e = new Engine(cli.getParameters());
		
		System.out.println(e.getMap());
		//e.step();
	}
}
