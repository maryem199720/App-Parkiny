import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

// Interface simple pour le contenu
export interface PageContent {
  title: string;
  description?: string;
  paragraphs?: string[];
  // Ajouté pour compatibilité avec la structure complexe
  [key: string]: any;
}

@Injectable({
  providedIn: 'root'
})
export class ContentService {
  // Contenu uniquement en français
  private contentData: { [page: string]: PageContent } = {
    'about': {
      title: 'À Propos de Parkiny',
      description: 'Parkiny est bien plus qu’un simple parking. C’est une solution innovante conçue pour transformer votre expérience de stationnement en Tunisie. Né d’un projet de fin d’études (PFE) ambitieux, Parkiny vise à démontrer comment la technologie utilise son intelligence pour une expérience quotidienne des automobilistes en offrant un service de parking sécurisé, sécurisée et pratique.',
      paragraphs: [
        'Parkiny est bien plus que’un simple parking. C’est une solution innovante conçue pour transformer votre expérience de stationnement en Tunisie. Né d’un projet de fin d’études (PFE) ambitieux, Parkiny vise à démontrer comment la technologie utilise son intelligence.',
        'Notre mission est de fluidifier et de sécuriser le stationnement dans les zones urbaines denses. Nous croyons une utilisation intelligente de la technologie pour offrir une expérience utilisateur sans tracas.',
        'Actuellement, nous sommes sur un site unique, stratégiquement situé à [Localisation Fictive, Tunis]. Le parking privé est ouvert 24/7 et offre une sécurité renforcée.'
      ]
    },
    'contact': {
      title: 'Contactez Parkiny',
      paragraphs: [
        'Vous avez une question ou besoin d’assistance ? Contactez-nous :',
        'Adresse : [Localisation Fictive, Tunis]',
        'Téléphone : [+216 XX XXX XXX]',
        'Email : [contact@parkiny-pfe.tn]',
        'Le parking est ouvert 24/7.'
      ]
    }
  };

  constructor() {}

  getPageContent(pageKey: 'about' | 'contact'): Observable<PageContent | undefined> {
    const content = this.contentData[pageKey];
    return of(content);
  }

  getAboutContent(): Observable<any> {
    return of({
      hero: {
        title: 'Stationnement Intelligent avec Détection de Plaques par IA',
        subtitle: 'Révolutionner le Stationnement en Tunisie'
      },
      executiveSummary: {
        title: 'Résumé Exécutif',
        description: 'Sur la base d’une recherche approfondie des principales solutions de stationnement intelligent et des exigences uniques du marché tunisien, nous proposons d’améliorer votre application de stationnement intelligent avec des capacités avancées de détection de plaques par IA et des fonctionnalités interactives. Ce document présente notre vision pour transformer votre application en une plateforme complète et engageante qui met en valeur votre technologie IA tout en résolvant les défis réels de stationnement en Tunisie.'
      },
      vision: {
        title: 'Vision et Proposition de Valeur',
        statement: 'Créer la première plateforme de stationnement intelligent en Tunisie qui exploite une technologie de détection de plaques par IA de pointe pour transformer l’expérience de stationnement urbain, réduire la congestion et offrir une solution fluide, sécurisée et conviviale pour les conducteurs et les opérateurs de parking.',
        content: 'Chez Parkiny, nous envisageons un avenir où le stationnement n’est plus une source de stress mais une expérience fluide et efficace. Notre mission est de transformer les infrastructures de stationnement existantes en espaces intelligents et connectés, en utilisant la technologie pour résoudre les défis quotidiens des conducteurs tunisiens.',
        keyPoints: ['Réduire le temps de recherche de stationnement', 'Diminuer la congestion urbaine', 'Améliorer l’expérience utilisateur', 'Contribuer à une ville plus intelligente et durable'],
        valueProposition: {
          drivers: [
            'Stationnement Sans Effort : La reconnaissance automatique des véhicules élimine le besoin de tickets, cartes ou entrées manuelles.',
            'Gain de Temps : Les informations sur la disponibilité en temps réel réduisent le temps de recherche jusqu’à 30%.',
            'Paiement Flexible : Payez uniquement pour l’utilisation réelle grâce à un suivi précis du temps.',
            'Sécurité Améliorée : Le suivi et la vérification des véhicules augmentent la sécurité et réduisent le stationnement non autorisé.'
          ],
          operators: [
            'Augmentation des Revenus : Une facturation précise et une réduction de la fraude peuvent augmenter les revenus jusqu’à 20%.',
            'Efficacité Opérationnelle : L’entrée/sortie automatisée réduit les besoins en personnel et les erreurs humaines.',
            'Insights Basés sur les Données : Des analyses avancées fournissent des informations précieuses sur les modèles d’utilisation et les opportunités d’optimisation.',
            'Solution Évolutive : L’architecture du système prend en charge la croissance, des petits parkings aux grandes installations.'
          ],
          urbanPlanners: [
            'Réduction de la Congestion : Une allocation efficace des places de stationnement réduit le trafic lié à la recherche de parking.',
            'Impact Environnemental : Moins de recherche de parking signifie moins d’émissions.',
            'Intégration aux Villes Intelligentes : La plateforme peut s’intégrer aux initiatives plus larges de ville intelligente.',
            'Données pour la Planification : Les données d’utilisation agrégées soutiennent de meilleures décisions de planification urbaine.'
          ]
        }
      },
      marketContext: {
        title: 'Contexte du Marché',
        globalTrends: [
          'Le marché mondial du stationnement intelligent connaît une croissance rapide, avec l’intégration de l’IA et de l’IoT devenant une norme.',
          'La technologie de reconnaissance de plaques d’immatriculation atteint une précision de 95 à 98 % dans des conditions optimales.',
          'Les solutions leaders évoluent vers des plateformes complètes combinant détection, paiement et analyses.'
        ],
        tunisianContext: [
          'Une augmentation de la possession de véhicules crée une pression accrue sur le stationnement dans les centres urbains.',
          'Une adoption limitée des solutions de stationnement automatisé par rapport aux marchés mondiaux.',
          'Des défis uniques, y compris des formats variés de plaques d’immatriculation et des préférences de paiement locales.',
          'Une opportunité d’établir un leadership sur le marché avec une approche axée sur l’IA.'
        ]
      },
      client: {
        name: 'TuniPark',
        founded: '2020',
        location: 'Tunis, Tunisie',
        description: 'TuniPark est une entreprise tunisienne spécialisée dans la gestion de parkings privés, cherchant à moderniser l’expérience de stationnement grâce à des solutions technologiques innovantes.'
      },
      coreTechnology: {
        title: 'Différenciateur Technologique Clé : Détection de Plaques par IA',
        description: 'Notre système de détection de plaques par IA offre une base solide avec plusieurs avantages clés, spécialement adaptés au marché tunisien.',
        content: 'Au cœur de Parkiny se trouve notre système propriétaire de reconnaissance de plaques d’immatriculation tunisiennes. Cette technologie d’IA est conçue pour identifier avec précision les différents formats de plaques tunisiennes.',
        details: [
          'Utilisation de YOLO pour la localisation des plaques d’immatriculation',
          'EasyOCR pour la lecture des caractères',
          'Capacité à identifier les différents formats tunisiens (civil, taxi, officiel, etc.)',
          'Extraction des parties numériques et inférence du texte arabe correct',
          'Gestion des problèmes courants comme la rotation d’image ou les erreurs de lecture'
        ],
        advantages: [
          'Reconnaissance précise et rapide',
          'Adaptation aux spécificités des plaques tunisiennes',
          'Fonctionnement dans diverses conditions d’éclairage et météorologiques',
          'Intégration facile avec les infrastructures existantes'
        ],
        features: [
          'Spécialisée pour les Plaques Tunisiennes : Reconnaît les formats de plaques tunisiennes (civil, taxi, officiel).',
          'Haute Précision : Utilise YOLO pour la localisation des plaques et EasyOCR pour la reconnaissance des caractères.',
          'Support du Texte Arabe : Extrait et infère le texte arabe correct associé aux types de plaques.',
          '  Gestion Robuste des Erreurs : Gestion de la rotation d’image et des erreurs de lecture pour une fiabilité accrue.'
        ]
      },
      competitiveAdvantages: {
        title: 'Avantages Concurrentiels',
        advantages: [
          'Technologie IA Localisée : Optimisée pour les plaques tunisiennes et le texte arabe.',
          'Mise en Œuvre Économique : Surveillance par une seule caméra de plusieurs places de stationnement.',
          'Double Vérification : Intégration de la recherche IA avec des capteurs IoT pour plus de fiabilité.',
          'Approche Mobile-First : Expérience complète sur application mobile adaptée.',
          'Déploiement Flexible : Convient aux environnements de stationnement intérieurs et extérieurs'
        ]
      },
      howItWorks: {
        title: 'Une Expérience de Stationnement Simplifiée',
        steps: [
          {
            title: 'Arrivée au Parking',
            description: 'La caméra détecte et reconnaît automatiquement la plaque d\'automatriculation.'
          },
          {
            title: 'Identification',
            description: 'Le système vérifie si le véhicule est enregistré ou dispose d’une réservation.'
          },
          {
            title: 'Accès',
            description: 'Entrée automatique pour les utilisateurs enregistrés ou paiements simples pour les nouveaux utilisateurs.'
          },
          {
            title: 'Stationnement',
            description: 'Guidage vers les places disponibles ou vers la place réservée.'
          },
          {
            title: 'Sortie',
            description: 'Reconnaissance automatique à la sortie et facturation sans intervention manuelle.'
          }
        ]
      },
      project: {
        title: 'Notre Projet',
        description: 'Parkiny a débuté comme un projet de fin d’études ambitieux en 2025. Notre vision était de créer une solution qui répond aux défis spécifiques du stationnement en Tunisie, en utilisant les dernières avancées technologiques de l’intelligence artificielle et en reconnaissance d’image.',
        features: ['Reconnaissance de plaques tunisiennes', 'Interface utilisateur intuitive', 'Système de réservation avancé', 'Résumé de données statistiques en temps']
      },
      team: {
        title: 'L’Équipe Derrière Parkiny',
        members: [
          {
            name: 'Maryem',
            role: 'Fondatrice & Développeuse Principale',
            bio: 'Experte en IA et vision par ordinateur.',
            image: 'https://via.placeholder.com/150'
          },
          {
            name: 'Membre de l’Équipe 2',
            role: 'Ingénieur IA',
            bio: 'Spécialiste en apprentissage automatique.',
            image: 'https://via.placeholder.com/'
          },
          {
            name: 'Membre de l’Équipe 3',
            role: 'Designer UX/UI',
            bio: 'Créateur d’expériences utilisateur intuitives.',
            image: 'https://via.placeholder.com/150'
          }
        ]
      },
      technology: {
        title: 'Notre Technologie',
        description: 'Au cœur de Parkiny se trouve un système de reconnaissance de plaques d’immatriculation conçu spécialement conçu pour le format tunisien. Cette technologie combine la vision par ordinateur (YOLO) pour la détection des plaques et l’OCR (EasyOCR) pour la lecture des caractères.',
        features: ['Adapté aux formats tunisiens', 'Haute précision même dans les conditions difficiles', 'Traitement en temps réel', 'Sécurité et confidentialité des données']
      },
      partners: {
        title: 'Ils Nous Font Confiance',
        list: ['Partenaire 1', 'Partenaire 2', 'Partenaire 3', 'Partenaire 4']
      },
      testimonials: {
        title: 'Ce Que Disent Nos Utilisateurs',
        list: [
          {
            name: 'Client 1',
            position: 'Gestionnaire de Parking',
            text: 'Parkiny a révolutionné notre façon de gérer notre parking. Le système de reconnaissance de plaques est incroyablement précis et a considérablement réduit les temps d’attente.'
          },
          {
            name: 'Client 2',
            position: 'Utilisateur Régulier',
            text: 'Je n’ai plus à me soucier de trouver une place ou de payer manuellement. Tout est automatisé.'
          }
        ]
      },
      impact: {
        title: 'Impact et Durabilité',
        points: ['Réduction des émissions de CO2', 'Optimisation des espaces de stationnement', 'Contribution à la ville intelligente tunisienne', 'Données précieuses pour l’urbanisme']
      },
      cta: {
        title: 'Rejoignez le Révolution du Stationnement Intelligent',
        buttons: ['Nous Contacter', 'Essayer Parkiny', 'En Savoir']
      },
      timeline: {
        title: 'Notre Parcours',
        events: [
          { date: 'Janvier 2025', title: 'Début du Projet', description: 'Analyse des besoins et spécifications.' },
          { date: 'Mars 2025', title: 'Développement du Prototype', description: 'Premier modèle fonctionnel de reconnaissance de plaques.' },
          { date: 'Mai 2025', title: 'Tests et Optimisation', description: 'Amélioration de la précision et des performances.' },
          { date: 'Juin 2025', title: 'Présentation Finale', description: 'Démonstration du système complet.' }
        ]
      }
    });
  }
}