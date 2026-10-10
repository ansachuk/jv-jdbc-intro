package mate.academy.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.academy.lib.Dao;
import mate.academy.lib.DataProcessingException;
import mate.academy.model.Book;
import mate.academy.util.DbConnection;

@Dao
public class BooksDaoImpl implements BooksDao {
    @Override
    public Book create(Book book) {
        final String sql = "INSERT INTO books (title, price) VALUES (?, ?)";

        try (
                Connection connection = DbConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, book.getTitle());
            statement.setBigDecimal(2, book.getPrice());

            int affectedRows = statement.executeUpdate();

            if (affectedRows < 1) {
                throw new RuntimeException("Expected to insert at lest one row");
            }
            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                Long bookId = generatedKeys.getObject(1, Long.class);
                book.setId(bookId);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add new book " + book, e);
        }

        return book;
    }

    @Override
    public Optional<Book> findById(Long id) {
        final String sql = "SELECT * FROM books WHERE id = ?";

        try (
                Connection connection = DbConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (
                    ResultSet resultSet = statement.executeQuery()
                    ) {
                if (resultSet.next()) {
                    return Optional.of(parseBook(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get book with id " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        final String sql = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();

        try (
                Connection connection = DbConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)) {

            try (
                    ResultSet resultSet = statement.executeQuery()
                    ) {
                while (resultSet.next()) {
                    books.add(parseBook(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get books", e);
        }

        return books;
    }

    @Override
    public Book update(Book book) {
        final String sql = "UPDATE books SET title = ?, price = ? WHERE id = ?";

        if (book.getId() == null) {
            throw new RuntimeException("Can't update the book without id " + book);
        }

        try (
                Connection connection = DbConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getTitle());
            statement.setBigDecimal(2, book.getPrice());
            statement.setLong(3, book.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows < 1) {
                throw new RuntimeException("Expected to insert at least one row");
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update book " + book, e);
        }

        return book;
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM books WHERE id = ?";

        try (
                Connection connection = DbConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            int affected = statement.executeUpdate();

            if (affected > 0) {
                return true;
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete book with id " + id, e);
        }

        return false;
    }

    private Book parseBook(ResultSet rs) throws SQLException {
        Long bookId = rs.getObject("id", Long.class);
        String title = rs.getString("title");
        BigDecimal price = rs.getObject("price", BigDecimal.class);

        return new Book(bookId, title, price);
    }
}
