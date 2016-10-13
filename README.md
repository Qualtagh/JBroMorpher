# JBroMorpher
Morphological analysis tool for Russian language

A very raw and very limited version.

Inspired by [pymorphy2](https://github.com/kmike/pymorphy2).

Uses [OpenCorpora](http://opencorpora.org/) dictionary.

Requires [DAWG](https://github.com/Qualtagh/DAWG) library.

First launch takes a long time (about 5 minutes with `-server` option on) to download and parse a dictionary and prepare a cached version.
Loading a cached version takes about 10 seconds. Dictionary file name resolving order:

- A local file `dict.opcorpora.xml` located in current directory
- `dict.opcorpora.xml.bz2`
- `dict.opcorpora.xml.zip`
- A file downloaded from Internet using link http://opencorpora.org/files/export/dict/dict.opcorpora.xml.bz2
- http://opencorpora.org/files/export/dict/dict.opcorpora.xml.zip

Set proxy if required:

    System.setProperty( "http.proxyHost", "localhost" );
    System.setProperty( "http.proxyPort", "3128" );

Or, use a method that accepts an exact file location argument.

A cached version is included in this repository.

Released into public domain.