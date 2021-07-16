.PHONY: all smoke

all: smoke

smoke:
	lein run -m agenta.experiment smoke
