.PHONY: all smoke test baseline demo

all: smoke

smoke:
	lein run -m agenta.experiment smoke

demo:
	lein run -m agenta.core

baseline:
	lein run -m agenta.experiment baseline

test:
	lein test
