.PHONY: all smoke test

all: smoke

smoke:
	lein run -m agenta.experiment smoke

test:
	lein test
