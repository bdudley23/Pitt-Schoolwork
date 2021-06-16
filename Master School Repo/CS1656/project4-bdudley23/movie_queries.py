from neo4j import GraphDatabase, basic_auth
import socket


class Movie_queries(object):
    def __init__(self, password):
        self.driver = GraphDatabase.driver("bolt://localhost", auth=("neo4j", password), encrypted=False)
        self.session = self.driver.session()
        self.transaction = self.session.begin_transaction()

    def q0(self):
        result = self.transaction.run("""
            MATCH (n:Actor) RETURN n.name, n.id ORDER BY n.birthday ASC LIMIT 3
        """)
        return [(r[0], r[1]) for r in result]

    def q1(self):
        result = self.transaction.run("""
            MATCH (a:Actor)-[:ACTS_IN]->() RETURN a.name, count(*) as cnt ORDER BY cnt DESC, a.name LIMIT 20
        """)
        return [(r[0], r[1]) for r in result]

    def q2(self):
        result = self.transaction.run("""
            MATCH (m:Movie)<-[r:RATED]-() WITH m, size(() -[:ACTS_IN]->(m)) as numact ORDER BY numact DESC LIMIT 1 RETURN m.title, numact
        """)
        return [(r[0], r[1]) for r in result]

    def q3(self):
        result = self.transaction.run("""
            MATCH (d:Director)-[:DIRECTED]->(m:Movie) WITH collect(DISTINCT m.genre) as genres, d WHERE length(genres) > 1 RETURN d.name, length(genres) ORDER BY length(genres) DESC, d.name
        """)
        return [(r[0], r[1]) for r in result]

    def q4(self):
        result = self.transaction.run("""
            MATCH (kb:Person {name: 'Kevin Bacon'})-[:ACTS_IN]->(movie1:Movie)<-[:ACTS_IN]-(coActor:Person)-[:ACTS_IN]->(movie2:Movie)<-[:ACTS_IN]-(coCoActor:Person)
            WHERE kb <> coCoActor
            AND NOT (kb)-[:ACTS_IN]->(:Movie)<-[:ACTS_IN]-(coCoActor)
            RETURN DISTINCT coCoActor.name ORDER BY coCoActor.name
            """)
        return [(r[0]) for r in result]

if __name__ == "__main__":
    sol = Movie_queries("neo4jpass")
    print("---------- Q0 ----------")
    print(sol.q0())
    print("---------- Q1 ----------")
    print(sol.q1())
    print("---------- Q2 ----------")
    print(sol.q2())
    print("---------- Q3 ----------")
    print(sol.q3())
    print("---------- Q4 ----------")
    print(sol.q4())
    sol.transaction.close()
    sol.session.close()
    sol.driver.close()

