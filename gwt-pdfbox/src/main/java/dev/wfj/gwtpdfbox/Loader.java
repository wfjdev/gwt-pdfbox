/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.wfj.gwtpdfbox;

import java.io.IOException;
import java.io.InputStream;

import dev.wfj.gwtpdfbox.io.IOUtils;
import dev.wfj.gwtpdfbox.io.RandomAccessStreamCache.StreamCacheCreateFunction;
import dev.wfj.gwtpdfbox.io.RandomAccessReadBuffer;
import dev.wfj.gwtpdfbox.io.RandomAccessRead;
import dev.wfj.gwtpdfbox.pdfparser.PDFParser;
import dev.wfj.gwtpdfbox.pdmodel.PDDocument;
import elemental2.dom.DomGlobal;

/**
 * Utility methods to load different types of documents
 *
 */
public class Loader
{
    private Loader()
    {
    }

    /**
     * Parses a PDF. Unrestricted main memory will be used for buffering PDF streams.
     * 
     * @param input byte array that contains the document. {@link dev.wfj.gwtpdfbox.io.RandomAccessReadBuffer} is used
     * to read the data.
     * 
     * @return loaded document
     * 
     * @throws InvalidPasswordException If the PDF required a non-empty password.
     * @throws IOException In case of a reading or parsing error.
     */
    public static PDDocument loadPDF(byte[] input) throws IOException
    {
        return Loader.loadPDF(input, "");
    }
    
    /**
     * Parses a PDF. Unrestricted main memory will be used for buffering PDF streams.
     * 
     * @param input byte array that contains the document. {@link dev.wfj.gwtpdfbox.io.RandomAccessReadBuffer} is used
     * to read the data.
     * @param password password to be used for decryption
     * 
     * @return loaded document
     * 
     * @throws InvalidPasswordException If the password is incorrect.
     * @throws IOException In case of a reading or parsing error.
     */
    public static PDDocument loadPDF(byte[] input, String password) throws IOException
    {
        return Loader.loadPDF(input, password, null, null);
    }
    
    /**
     * Parses a PDF. Unrestricted main memory will be used for buffering PDF streams.
     * 
     * @param input byte array that contains the document. {@link dev.wfj.gwtpdfbox.io.RandomAccessReadBuffer} is used
     * to read the data.
     * @param password password to be used for decryption
     * @param keyStore key store to be used for decryption when using public key security
     * @param alias alias to be used for decryption when using public key security
     * 
     * @return loaded document
     * 
     * @throws InvalidPasswordException If the password is incorrect.
     * @throws IOException In case of a reading or parsing error.
     */
    public static PDDocument loadPDF(byte[] input, String password, InputStream keyStore, String alias)
            throws IOException
    {
        return Loader.loadPDF(input, password, keyStore, alias, IOUtils.createMemoryOnlyStreamCache());
    }
    
    /**
     * Parses a PDF.
     * 
     * @param input byte array that contains the document. {@link dev.wfj.gwtpdfbox.io.RandomAccessReadBuffer} is used
     * to read the data.
     * @param password password to be used for decryption
     * @param keyStore key store to be used for decryption when using public key security
     * @param alias alias to be used for decryption when using public key security
     * @param streamCacheCreateFunction a function to create an instance of a stream cache to be used for buffering
     * new/altered PDF streams
     * 
     * @return loaded document
     * 
     * @throws InvalidPasswordException If the password is incorrect.
     * @throws IOException In case of a reading or parsing error.
     */
    public static PDDocument loadPDF(byte[] input, String password, InputStream keyStore, String alias,
            StreamCacheCreateFunction streamCacheCreateFunction) throws IOException
    {
        RandomAccessRead source = null;
        try
        {
            // RandomAccessRead is not closed here, may be needed for signing
            source = new RandomAccessReadBuffer(input);
            PDFParser parser = new PDFParser(source, password, keyStore, alias, streamCacheCreateFunction);
            return parser.parse();
        }
        catch (IOException ioe)
        {
            DomGlobal.console.log(ioe);
            IOUtils.closeQuietly(source);
            throw ioe;
        }
    }

    /**
     * Parses a PDF. Unrestricted main memory will be used for buffering PDF new streams.
     * 
     * @param randomAccessRead random access read representing the pdf to be loaded
     * 
     * @return loaded document
     * 
     * @throws InvalidPasswordException If the PDF required a non-empty password.
     * @throws IOException In case of a reading or parsing error.
     */
    public static PDDocument loadPDF(RandomAccessRead randomAccessRead) throws IOException
    {
        return Loader.loadPDF(randomAccessRead, "", null, null, IOUtils.createMemoryOnlyStreamCache());
    }

    /**
     * Parses a PDF.
     * 
     * @param randomAccessRead random access read representing the pdf to be loaded
     * @param streamCacheCreateFunction a function to create an instance of a stream cache to be used for buffering
     * new/altered PDF streams
     * 
     * @return loaded document
     * 
     * @throws InvalidPasswordException If the PDF required a non-empty password.
     * @throws IOException In case of a reading or parsing error.
     */
    public static PDDocument loadPDF(RandomAccessRead randomAccessRead,
            StreamCacheCreateFunction streamCacheCreateFunction)
            throws IOException
    {
        return Loader.loadPDF(randomAccessRead, "", null, null, streamCacheCreateFunction);
    }

    /**
     * Parses a PDF. Unrestricted main memory will be used for buffering new/altered PDF streams.
     *
     * @param randomAccessRead random access read representing the pdf to be loaded
     * @param password password to be used for decryption
     *
     * @return loaded document
     * 
     * @throws InvalidPasswordException If the password is incorrect.
     * @throws IOException In case of a reading or parsing error.
     */
    public static PDDocument loadPDF(RandomAccessRead randomAccessRead, String password) throws IOException
    {
        return Loader.loadPDF(randomAccessRead, password, null, null,
                IOUtils.createMemoryOnlyStreamCache());
    }

    /**
     * Parses a PDF. Unrestricted main memory will be used for buffering new/altered PDF streams.
     *
     * @param randomAccessRead random access read representing the pdf to be loaded
     * @param password password to be used for decryption
     * @param keyStore key store to be used for decryption when using public key security
     * @param alias alias to be used for decryption when using public key security
     * 
     * @return loaded document
     * 
     * @throws IOException In case of a reading or parsing error.
     */
    public static PDDocument loadPDF(RandomAccessRead randomAccessRead, String password, InputStream keyStore,
            String alias) throws IOException
    {
        return Loader.loadPDF(randomAccessRead, password, keyStore, alias,
                IOUtils.createMemoryOnlyStreamCache());
    }

    /**
     * Parses a PDF.
     *
     * @param randomAccessRead random access read representing the pdf to be loaded
     * @param password password to be used for decryption
     * @param streamCacheCreateFunction a function to create an instance of a stream cache to be used for buffering
     * new/altered PDF streams
     * 
     * @return loaded document
     * 
     * @throws InvalidPasswordException If the password is incorrect.
     * @throws IOException In case of a reading or parsing error.
     */
    public static PDDocument loadPDF(RandomAccessRead randomAccessRead, String password,
            StreamCacheCreateFunction streamCacheCreateFunction) throws IOException
    {
        return Loader.loadPDF(randomAccessRead, password, null, null, streamCacheCreateFunction);
    }

    /**
     * Parses a PDF.
     * 
     * @param randomAccessRead random access read representing the pdf to be loaded
     * @param password password to be used for decryption
     * @param keyStore key store to be used for decryption when using public key security
     * @param alias alias to be used for decryption when using public key security
     * @param streamCacheCreateFunction a function to create an instance of a stream cache to be used for buffering
     * new/altered PDF streams
     * 
     * @return loaded document
     * 
     * @throws IOException in case of a file reading or parsing error
     */
    public static PDDocument loadPDF(RandomAccessRead randomAccessRead, String password,
            InputStream keyStore, String alias, StreamCacheCreateFunction streamCacheCreateFunction)
            throws IOException
    {
        PDFParser parser = new PDFParser(randomAccessRead, password, keyStore, alias,
                streamCacheCreateFunction);
        return parser.parse();
    }

}
