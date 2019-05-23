package dbwrapper

import (
	"github.com/dgraph-io/badger"
)

var opened *badger.DB
var dbhelper int

type SimpleDBW struct {
	Badger *badger.DB
}

func NewSimpleDBW() *SimpleDBW {
	db := &opened
	return &SimpleDBW{*db}
}

func OpenDB(dir string) {
	if dbhelper == 0 {
		opts := badger.DefaultOptions // optimize for smartphones
		opts.Dir = dir
		opts.ValueDir = dir
		opening, err := badger.Open(opts)
		handle(err)
		//db.Badger = opening
		opened = opening
		dbhelper = 1
	}
}

func CloseDB() {
	db := &opened
	dbw := *db
	dbw.Close()
	dbhelper = 0
}

func (db *SimpleDBW) Update(key, value string) {
	db.Badger.Update(func(txn *badger.Txn) error {
		err := txn.Set([]byte(key), []byte(value))
		return err
	})
}

func (db *SimpleDBW) View(key string) string {
	var varDBentry []byte
	// badger view entry function
	db.Badger.View(func(txn *badger.Txn) error {
		item, _ := txn.Get([]byte(key))
		item.Value(func(val []byte) error {
			varDBentry = append([]byte{}, val...)
			return nil
		})
		return nil
	})
	return bytesToString(varDBentry)
}

func bytesToString(data []byte) string {
	//fmt.Println("converted bytes to string")
	return string(data[:])
}

func handle(err error) {
	// todo
}
