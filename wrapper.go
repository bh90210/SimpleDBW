package dbwrapper

import (
	"github.com/dgraph-io/badger"
	"github.com/dgraph-io/badger/options"
)

// helpers
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
func (db *SimpleDBW) Update(key, value []byte) {
	err := db.Badger.Update(func(txn *badger.Txn) error {
		err := txn.Set(key, value)
		return err
	})
	handle(err)
}

// PrePopulate database function
func (db *SimpleDBW) PrePopulate(key, value []byte) {
	keyHelper := append(key, "_HELPER"...)
	init := db.View(key)
	inittoString := string(init)
	if inittoString == "1" {
		// if true means pre-populate has already ran so do nothing
	} else {
		db.Update(key, value)
		db.Update(keyHelper, []byte("1"))
	}
}

// View method for viewing a key
func (db *SimpleDBW) View(key []byte) []byte {
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
	return varDBentry
}

// Delete method for deleting a key
func (db *SimpleDBW) Delete(key []byte) {
	db.Badger.Update(func(txn *badger.Txn) error {
		err := txn.Delete(key)
		handle(err)
		return nil
	})
}

// DropPrefix method for droping all keys related to specific prefix
func (db *SimpleDBW) DropPrefix(prefix []byte) {
	err := db.Badger.DropPrefix(prefix)
	handle(err)
}

// DropAll method for droping the whole database
func (db *SimpleDBW) DropAll() {
	err := db.Badger.DropAll()
	handle(err)
}

var store = make(map[int][]byte)

func (db *SimpleDBW) KeyOnlyIterator() int {
	var i int
	err := db.Badger.View(func(txn *badger.Txn) error {
		opts := badger.DefaultIteratorOptions
		opts.PrefetchValues = false
		it := txn.NewIterator(opts)
		defer it.Close()
		for it.Rewind(); it.Valid(); it.Next() {
			item := it.Item()
			k := item.Key()
			store[i] = k
			i++
		}
		return nil
	})
	handle(err)
	return i
}

func (db *SimpleDBW) KeyOnlyIteratorPrefix(prefix []byte) int {
	var i int
	err := db.Badger.View(func(txn *badger.Txn) error {
		opts := badger.DefaultIteratorOptions
		opts.PrefetchValues = false
		it := txn.NewIterator(opts)
		defer it.Close()
		for it.Seek(prefix); it.ValidForPrefix(prefix); it.Next() {
			item := it.Item()
			k := item.Key()
			store[i] = k
			i++
		}
		return nil
	})
	handle(err)
	return i
}

func Dump(i int) []byte {
	key := store[i]
	return key
}

// IncInt method for monotonically increasing integers - ie. notifications id
func (db *SimpleDBW) IncInt(key []byte) int {
	seq, errGetSequence := db.Badger.GetSequence([]byte(key), 1000)
	defer seq.Release()
	handle(errGetSequence)
	num, errNext := seq.Next()
	handle(errNext)
	return int(num)
}

/*
// TTL Time To Live database keys with specified lifetime
func (db *SimpleDBW) Ttl(key, value, expiration string) {
	expi, errParse := time.ParseDuration(expiration)
	handle(errParse)

	// Start a writable transaction.
	txn := db.Badger.NewTransaction(true)
	defer txn.Discard()

	// Use the transaction...
	errTTL := txn.SetWithTTL([]byte(key), []byte(value), expi)
	handle(errTTL)

	// Commit the transaction and check for error.
	errCommit := txn.Commit()
	handle(errCommit)
}

// Backup database
func (db *SimpleDBW) Backup(dir string) {
	var since uint64
	timestamp := db.View("LATEST_BACKUP_TIMESTAMP")

	//f, errCreate := os.Create(path.Join(dir, "database.bak"))
	f, errCreate := os.Create(dir)
	defer f.Close()
	handle(errCreate)
	w := bufio.NewWriter(f)

	if timestamp == "key does not exist" {
		now := time.Now()
		since = uint64(now.Unix())
	} else {
		u, errParse := strconv.ParseUint(timestamp, 10, 64)
		handle(errParse)
		since = u
	}

	newTimestamp, errTimeStamp := db.Badger.Backup(w, since)
	handle(errTimeStamp)
	s := strconv.FormatUint(newTimestamp, 10)
	db.Update("LATEST_BACKUP_TIMESTAMP", s)
}

// Restore database
func (db *SimpleDBW) Restore(dir string) {
	//f, erroOpen := os.Open(path.Join(dir, "database.bak"))
	f, erroOpen := os.Open(dir)
	defer f.Close()
	handle(erroOpen)
	r := bufio.NewReader(f)
	errLoad := db.Badger.Load(r)
	handle(errLoad)
}
*/

// TODO for v1.1 Stream implementation
