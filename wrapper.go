package dbwrapper

import (
	"github.com/dgraph-io/badger"
	"github.com/dgraph-io/badger/options"
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
		opts := badger.DefaultOptions
		opts.Dir = dir
		opts.ValueDir = dir

		// badger memory usage optimisation
		opts.NumCompactors = 1
		opts.ValueLogLoadingMode = options.FileIO
		opts.TableLoadingMode = options.FileIO
		opts.NumLevelZeroTables = 3
		opts.NumLevelZeroTablesStall = 7
		opts.NumMemtables = 3

		opening, err := badger.Open(opts)
		handle(err)
		//db.Badger = opening
		opened = opening
		dbhelper = 1
	}
}

func CloseDB() {
	if dbhelper == 1 {
		db := &opened
		dbw := *db
		dbw.Close()
		dbhelper = 0
	}
}

func bytesToString(data []byte) string {
	//fmt.Println("converted bytes to string")
	return string(data[:])
}

func handle(err error) {
	// todo
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
		item, err := txn.Get([]byte(key))
		//handle(err)
		if err != nil {
		    varDBentry = []byte("Key does not exist")
		    return err
		}

		item.Value(func(val []byte) error {
			varDBentry = append([]byte{}, val...)
			return nil
		})
		return nil
	})
	return bytesToString(varDBentry)
}

// Delete key

// Iterating over keys

// - Prefix scans

// - Key-only iteration

// Merge Operations

// Monotonically increasing integers

// Setting Time To Live(TTL) and User Metadata on Keys

// Stream

// Database backup

// Pseudo relational layer

// - Experimental key-only

// - Array implementaion?
