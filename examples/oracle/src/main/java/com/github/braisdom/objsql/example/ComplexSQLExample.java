package com.github.braisdom.objsql.example;

import com.github.braisdom.objsql.Databases;
import com.github.braisdom.objsql.example.domains.Member;
import com.github.braisdom.objsql.example.domains.Order;
import com.github.braisdom.objsql.pagination.Page;
import com.github.braisdom.objsql.pagination.PagedList;
import com.github.braisdom.objsql.pagination.Paginator;
import com.github.braisdom.objsql.sql.Select;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.github.braisdom.objsql.sql.function.Ansi.count;

public class ComplexSQLExample extends OracleExample {

    private static final String[] MEMBER_NAMES = {"Joe", "Juan", "Jack", "Albert", "Jonathan", "Justin", "Terry", "Gerald", "Keith", "Samuel",
            "Willie", "Ralph", "Lawrence", "Nicholas", "Roy", "Benjamin", "Bruce", "Brandon", "Adam", "Harry", "Fred", "Wayne", "Billy", "Steve",
            "Louis", "Jeremy", "Aaron", "Randy", "Howard", "Eugene", "Carlos", "Russell", "Bobby", "Victor", "Martin", "Ernest", "Phillip", "Todd",
            "Jesse", "Craig", "Alan", "Shawn", "Clarence", "Sean", "Philip", "Chris", "Johnny", "Earl", "Jimmy", "Antonio", "James", "John", "Robert",
            "Michael", "William", "David", "Richard", "Charles", "Joseph", "Thomas", "Christopher", "Daniel", "Paul", "Mark", "Donald", "George",
            "Kenneth", "Steven", "Edward", "Brian", "Ronald", "Anthony", "Kevin", "Jason", "Matthew", "Gary", "Timothy", "Jose", "Larry", "Jeffrey",
            "Frank", "Scott", "Eric", "Stephen", "Andrew", "Raymond", "Gregory", "Joshua", "Jerry", "Dennis", "Walter", "Patrick", "Peter", "Harold",
            "Douglas", "Henry", "Carl", "Arthur", "Ryan", "Roger"};

    @Test
    public void prepareQueryData() throws SQLException {
        List<Member> members = new ArrayList<>();
        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            members.add(new Member()
                    .setNo("Q200000" + i)
                    .setName(MEMBER_NAMES[i])
                    .setGender(0)
                    .setMobile("150000000" + i));
        }

        for (int i = 0; i < 100; i++) {
            orders.add(new Order()
                    .setNo("20200000" + i)
                    .setMemberId((long) i)
                    .setAmount(RandomUtils.nextFloat(10.0f, 30.0f))
                    .setQuantity(RandomUtils.nextFloat(100.0f, 300.0f))
                    .setSalesAt(Timestamp.valueOf("2020-05-01 09:30:00")));
        }

        int[] createdMembersCount = Member.create(members.toArray(new Member[]{}),
                true);
        int[] createdOrderCount = Order.create(orders.toArray(new Order[]{}),
                true);

        Assert.assertEquals(createdMembersCount.length, 100);
        Assert.assertEquals(createdOrderCount.length, 100);
    }

    @Test
    public void simpleQuery() throws SQLException {
        prepareQueryData();

        Member.Table member = Member.asTable();
        Select select = new Select();

        select.project(member.gender, count().as("member_count"))
                .from(member)
                .groupBy(member.gender);

        List<Member> members = select.execute(Member.class);

        Assert.assertTrue(members.size() > 0);
        Assert.assertTrue(((BigDecimal)members.get(0).getRawAttribute("MEMBER_COUNT")).longValue() == 100);
    }

    @Test
    public void joinQuery() throws SQLException {
        prepareQueryData();

        Member.Table member = Member.asTable();
        Order.Table order = Order.asTable();

        Select select = new Select();

        select.project(member.no, member.name, count().as("order_count"))
                .from(member, order)
                .where(member.id.eq(order.memberId))
                .groupBy(member.no, member.name);

        List<Member> members = select.execute(Member.class);
        Assert.assertTrue(members.size() > 0);
    }

    @Test
    public void pagedQuery() throws SQLException {
        prepareQueryData();

        Member.Table member = Member.asTable();
        Order.Table order = Order.asTable();
        Paginator<Member> paginator = Databases.getPaginator();
        Page page = Page.create(0, 20);

        Select select = new Select();

        select.project(member.no, member.name, count().as("order_count"))
                .from(member, order)
                .where(member.id.eq(order.memberId))
                .groupBy(member.no, member.name);

        PagedList<Member> members = paginator.paginate(page, select, Member.class);
        Assert.assertTrue(members.size() > 0);
    }
}
