package dbwrapper

import (
	_ "github.com/dgraph-io/badger"
)

type Counter struct {
	Value int
}

func (c *Counter) Inc() { c.Value++ }

func NewCounter() *Counter { return &Counter{ 5 } }

type Printer interface {
	Print(s string)
}

func PrintHello(p Printer) {
	p.Print("Hello, World!")
}
