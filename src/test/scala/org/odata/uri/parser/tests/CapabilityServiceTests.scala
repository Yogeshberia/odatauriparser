package org.odata.uri.parser.tests

import org.scalatest.FunSuite
import org.odata.uri.parser._
import org.odata.jpa.{JPAQueryExecutor, EntityManagerService}
import org.odata.jpa.model.{Capability, CapabilityKind}
import org.slf4j.{Logger, LoggerFactory}
import javax.persistence.criteria.CriteriaBuilder
import org.hibernate.Query
import javax.persistence.EntityManager

class CapabilityServiceTests extends FunSuite{

  def LOG:Logger = LoggerFactory.getLogger(classOf[CapabilityServiceTests])
  val p = new ODataUriParser
  val mainParser = p.oDataQuery


  test("Basic capability tests") {
    val em = EntityManagerService.getEntityManager

    em.getTransaction.begin()

    var typeHttp = new CapabilityKind("transport.endpoint.http")
    var typeFTP = new CapabilityKind("transport.endpoint.http")

    em.persist(typeHttp)
    em.persist(typeFTP)

    def capabilityHttpLive =  new Capability("http://live.sdl.com", typeHttp)
    def capabilityHttpStaging =  new Capability("http://staging.sdl.com", typeHttp)

    em.persist(capabilityHttpLive)
    em.persist(capabilityHttpStaging)

    em.getTransaction().commit()

    em.refresh(typeHttp)

    LOG.error(em.createQuery("From CapabilityKind", classOf[CapabilityKind] ).getResultList().toString)
    LOG.error(em.createQuery("From Capability", classOf[Capability] ).getResultList().toString)
    LOG.error("Capabilities: {}" + typeHttp.capabilities)

    em.close()

  }



  test("Generate JPQL /Capability?$select=value"){

    val uri = "http://services.odata.org/OData.svc/Capability?$select=value"
    val actual = p.parseThis(mainParser, uri).get
    /*
    val expectedAst =
      ODataQuery(
        URL("http://services.odata.org/OData.svc"),
        ResourcePath("Capability",EmptyExp(),EmptyExp()),
        QueryOperations(List(Select(List(Property("value"))))))
    */

    def result = JPAQueryExecutor(EntityManagerService.getEntityManager, actual.asInstanceOf[ODataQuery]).execute()

    println("Result: " + result)

  }

  test("Generate JPQL /Capability?$select=id"){

    val uri = "http://services.odata.org/OData.svc/Capability?$select=id"
    val actual = p.parseThis(mainParser, uri).get

    def result = JPAQueryExecutor(EntityManagerService.getEntityManager, actual.asInstanceOf[ODataQuery]).execute()

    println("Result: " + result)

  }

  test("Generate JPQL /Capability?$select=id,value"){

    val uri = "http://services.odata.org/OData.svc/Capability?$select=id,value"
    val actual = p.parseThis(mainParser, uri).get

    def result = JPAQueryExecutor(EntityManagerService.getEntityManager, actual.asInstanceOf[ODataQuery]).execute()

    println("Result: " + result)

  }



}