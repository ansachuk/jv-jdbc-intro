package mate.academy;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.dao.BooksDao;
import mate.academy.lib.Injector;
import mate.academy.model.Book;

public class Main {
    private static final Injector injector =
            Injector.getInstance("mate.academy");
    private static final Long ID = 7L;
    private static final String TITLE = "The Dark Tower";
    private static final BigDecimal PRICE = new BigDecimal(15);

    public static void main(String[] args) {
        BooksDao booksDao = (BooksDao) injector.getInstance(BooksDao.class);

        Book book = new Book(TITLE, PRICE);
        booksDao.create(book);

        Book byId = booksDao.findById(ID).orElseThrow(() ->
                new RuntimeException("Can't get book with id: " + ID));
        byId.setTitle("1924");
        byId.setPrice(new BigDecimal(16));

        booksDao.update(byId);
        booksDao.deleteById(ID);

        List<Book> all = booksDao.findAll();
        System.out.println("all = " + all);

    }
}
