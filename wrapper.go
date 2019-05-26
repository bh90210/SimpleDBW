package dbwrapper

import (
	"time"

	"github.com/dgraph-io/badger"
	"github.com/dgraph-io/badger/options"
)

// helpers
func bytesToString(data []byte) string {
	//fmt.Println("converted bytes to string")
	return string(data[:])
}
func handle(err error) {
	if err != nil {
		errorHandleHelper(err)
	}
}
func errorHandleHelper(err error) error {
	return err
}

var opened *badger.DB
var dbhelper int

// SimpleDBW database struct for use in view, update etc methods
type SimpleDBW struct {
	Badger *badger.DB
}

// NewSimpleDBW passing the database to android
func NewSimpleDBW() *SimpleDBW {
	db := &opened
	ret := &SimpleDBW{*db}
	return ret
}

// OpenDB package function for initialising the database
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
		opts.NumLevelZeroTablesStall = 6
		opts.NumMemtables = 3

		opening, err := badger.Open(opts)
		handle(err)
		//db.Badger = opening
		opened = opening
		dbhelper = 1
	}
}

// CloseDB package function for closing the database
func CloseDB() {
	// check if database is open
	if dbhelper == 1 {
		db := &opened
		dbw := *db
		err := dbw.Close()
		handle(err)
		dbhelper = 0
	}
}

// Update method for updating a key
func (db *SimpleDBW) Update(key, value string) {
	err := db.Badger.Update(func(txn *badger.Txn) error {
		err := txn.Set([]byte(key), []byte(value))
		return err
	})
	handle(err)
}

// PrePopulate database function
func (db *SimpleDBW) PrePopulate(key, value string) {
	init := db.View(key)
	if init == "key does not exist" {
		db.Update(key, value)
	}
}

// View method for viewing a key
func (db *SimpleDBW) View(key string) string {
	var varDBentry []byte
	// badger view entry function
	db.Badger.View(func(txn *badger.Txn) error {
		item, errGET := txn.Get([]byte(key))
		if errGET != nil {
			varDBentry = []byte("key does not exist")
			return errGET
		}
		errValue := item.Value(func(val []byte) error {
			varDBentry = append([]byte{}, val...)
			return nil
		})
		handle(errValue)
		return nil
	})
	return bytesToString(varDBentry)
}

// Delete method for deleting a key
func (db *SimpleDBW) Delete(key string) {
	db.Badger.Update(func(txn *badger.Txn) error {
		err := txn.Delete([]byte(key))
		handle(err)
		return nil
	})
}

// DropPrefix method for droping all keys related to specific prefix
func (db *SimpleDBW) DropPrefix(prefix string) {
	err := db.Badger.DropPrefix([]byte(prefix))
	handle(err)
}

// DropAll method for droping the whole database
func (db *SimpleDBW) DropAll() {
	err := db.Badger.DropAll()
	handle(err)
}

// Iterating over keys - seek() - dump

// - Prefix scans

// - Key-only iteration

// IncInt method for monotonically increasing integers - ie. notifications id
func (db *SimpleDBW) IncInt(key string) int {
	seq, errGetSequence := db.Badger.GetSequence([]byte(key), 1000)
	defer seq.Release()
	handle(errGetSequence)
	num, errNext := seq.Next()
	handle(errNext)
	return int(num)
}

// TTL Time To Live database keys with specified lifetime
func (db *SimpleDBW) TTL(key, value, inputTime string) {
	// convert input from string to Time
	expiration, errParse := time.Parse("ANSIC", inputTime)
	handle(errParse)
	duration := time.Until(expiration)
	errUpdate := db.Badger.Update(func(txn *badger.Txn) error {
		errTTL := txn.SetWithTTL([]byte(key), []byte(value), duration)
		handle(errTTL)
		return nil
	})
	handle(errUpdate)
}

/*
// Backup database
func (db *SimpleDBW) Backup() {
	// get latest timestamp stored in db from last backup
	sinceString := db.View("LATEST_BACKUP_TIMESTAMP_HELPER")
	// check to see if this is the first time backup is called
	// if true the above db.View query will return an "error" string
	// change that to 0 so it can be converted to uint64
	if sinceString == "key does not exist" {
		sinceString = "0"
	}
	// convert front string to uint64
	since, errIntConv := strconv.ParseUint(sinceString, 10, 64)
	handle(errIntConv)
	// perform the backup operation and get and new timestamp
	newTimestamp, errTimeStamp := db.Badger.Backup(w, since)
	handle(errTimeStamp)
	// store new timestamp in db for next backup
	db.Update("LATEST_BACKUP_TIMESTAMP_HELPER", string(newTimestamp))
}

// Restore database
func (db *SimpleDBW) Restore() {
	err := db.Badger.Load(r)
	handle(err)
}
*/
// TODO for v1.1  Pseudo relational layer

// TODO for v1.1 - Merge Operations

// TODO for v1.1 - Experimental key-only

// TODO for v1.1 Stream implementation
