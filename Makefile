.PHONY: all smoke test

all: smoke

smoke:
	lein run -m agenta.experiment smoke

baseline:
	lein run -m agenta.experiment baseline

test:
	lein test
