// IBookManagerInterface.aidl
package com.sparkfengbo.app.javabcsxtest.aidltest;

// Declare any non-default types here with import statements
import com.sparkfengbo.app.javabcsxtest.aidltest.Book;

interface IBookManagerInterface {

   List<Book> getList();

   void addBook(in Book book);
}